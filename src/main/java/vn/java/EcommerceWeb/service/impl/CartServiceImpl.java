package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.java.EcommerceWeb.dto.request.AddToCartRequest;
import vn.java.EcommerceWeb.dto.request.RemoveCartRequest;
import vn.java.EcommerceWeb.dto.response.CartProductResponse;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ProductResponse;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.Cart;
import vn.java.EcommerceWeb.model.CartItem;
import vn.java.EcommerceWeb.model.Product;
import vn.java.EcommerceWeb.model.User;
import vn.java.EcommerceWeb.repository.*;
import vn.java.EcommerceWeb.service.CartService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public void addProductToCart(AddToCartRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = user.getCart();
        if (cart == null) {
            cart = Cart.builder().user(user).cartItems(new HashSet<>()).build();
            user.setCart(cart);
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        //Tim xem san pham co trong gio hang chua
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);
        Integer quantity = request.getQuantity() == null ? 1 : request.getQuantity();
        if (cartItem == null) {
            cartItem = CartItem.builder().cart(cart).product(product).quantity(quantity).build();
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartRepository.save(cart);
    }

    @Override
    public void removeProductToCart(RemoveCartRequest request) {
        log.info("Remove cart starting...");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Product> listProduct = productRepository.findAllById(request.getProductIds());
        log.info("User: {}", user);
        log.info("List Product: {}", listProduct);
        Cart cart = user.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        cart.getCartItems().removeIf(item -> listProduct.contains(item.getProduct()));
        cartRepository.save(cart);
        log.info("Cart: {}", cart.getCartItems().size());
        log.info("Remove cart successfully");
    }

    @Override
    public PageResponse<?> getAllProductInCart(Long userId, int pageNo, int pageSize, String sortBy) {
        log.info("Get all product in cart starting...");
        if (pageNo > 0) {
            pageNo = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase(("asc"))) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = user.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId(), pageable);

        List<CartProductResponse> productList = cartItems.stream()
                .map(cartItem -> CartProductResponse.builder()
                        .productId(cartItem.getProduct().getId())
                        .name(cartItem.getProduct().getName())
                        .image(cartItem.getProduct().getImage())
                        .price(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .category(cartItem.getProduct().getCategory().getName())
                        .totalPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity())
                        .build())
                .collect(Collectors.toList());
        log.info("Get all product in cart successfully");

        return PageResponse.builder()
                .pageNo(pageNo + 1)
                .pageSize(pageSize)
                .totalPage(cartItems.getTotalPages())
                .totalElement(cartItems.getTotalElements())
                .items(productList)
                .build();
    }
}
