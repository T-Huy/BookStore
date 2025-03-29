package vn.java.EcommerceWeb.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.java.EcommerceWeb.dto.request.CartItemRequest;
import vn.java.EcommerceWeb.dto.request.OrderRequest;
import vn.java.EcommerceWeb.enums.OrderStatus;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.*;
import vn.java.EcommerceWeb.repository.*;
import vn.java.EcommerceWeb.service.MailService;
import vn.java.EcommerceWeb.service.OrderService;
import vn.java.EcommerceWeb.service.PaymentService;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MailService mailService;

    @Override
    public void createOrder(OrderRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Create order starting...");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Long> productIds = request.getCartItems()
                .stream()
                .map(item -> item.getProductId())
                .collect(Collectors.toList());

        List<CartItem> cartItemList = cartItemRepository.findAllByProductIdIn(productIds);
        if (cartItemList.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products are not in cart");
        }

        //Lay toan bo san pham mot lan duy nhat
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        //Kiem tra san pham va stock
        for (CartItemRequest cartItemRequest : request.getCartItems()) {
            Product product = productMap.get(cartItemRequest.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found");
            }
            if (product.getStock() < cartItemRequest.getQuantity()) {
                throw new ResourceNotFoundException("Product" + product.getName() + "out of stock");
            }
        }

        //Tao order
        Order order = Order.builder()
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .recipientAddress(request.getRecipientAddress())
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(request.getCartItems().stream().mapToDouble(item -> {
                    Product product = productMap.get(item.getProductId());
                    return product.getPrice() * item.getQuantity();
                }).sum())
                .user(user)
                .orderItems(new HashSet<>())
                .build();

        //Tao order item va tru stock trong product
        for (CartItemRequest cartItemRequest : request.getCartItems()) {
            Product product = productMap.get(cartItemRequest.getProductId());

            OrderItem orderItem = OrderItem.builder()
                    .image(product.getImage())
                    .name(product.getName())
                    .quantity(cartItemRequest.getQuantity())
                    .priceAtOrder(product.getPrice())
                    .product(product)
                    .order(order)
                    .build();
            order.getOrderItems().add(orderItem);

            product.setStock(product.getStock() - cartItemRequest.getQuantity());


        }
        productRepository.saveAll(productMap.values());
        orderRepository.save(order);

        Payment payment = paymentService.processPayment(order,cartItemList, request.getPaymentMethod());

        orderRepository.save(order);
        String mailTitle = "BookStore - Xác nhận đơn hàng #" + order.getId() + " của bạn thành công";

        // Mẫu HTML
        String content = """
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px; }
                            .container { max-width: 600px; background: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }
                            .header { text-align: center; background: #4CAF50; color: white; padding: 15px; border-radius: 10px 10px 0 0; }
                            .content { padding: 20px; }
                            .order-details { background: #f9f9f9; padding: 15px; border-radius: 8px; }
                            .product-table { width: 100%%; border-collapse: collapse; margin: 10px 0; }
                            .product-table th, .product-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                            .product-table th { background: #4CAF50; color: white; }
                            .total { font-size: 18px; font-weight: bold; color: #e67e22; text-align: right; margin-top: 10px; }
                            .button { display: inline-block; padding: 10px 15px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px; }
                            .footer { text-align: center; padding: 15px; font-size: 14px; color: #555; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>Xác nhận đơn hàng</h2>
                            </div>
                            <div class="content">
                                <p>Xin chào <strong>%s</strong>,</p>
                                <p>Cảm ơn bạn đã đặt hàng tại <strong>%s</strong>! Chúng tôi đã nhận được đơn hàng của bạn và đang xử lý.</p>

                                <div class="order-details">
                                    <p><strong>📌 Mã đơn hàng:</strong> %s</p>
                                    <p><strong>📆 Ngày đặt hàng:</strong> %s</p>
                                    <p><strong>📍 Địa chỉ nhận hàng:</strong> %s</p>
                                    
                                    <p><strong>🛒 Sản phẩm đã đặt:</strong></p>
                                    <table class="product-table">
                                        <thead>
                                            <tr>
                                                <th>Tên sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Đơn giá</th>
                                                <th>Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>

                                    <p class="total">💰 Tổng tiền: %,.0f VND</p>
                                    <p><strong>💳 Phương thức thanh toán:</strong> %s</p>
                                    <p><strong>💳 Trạng thái thanh toán:</strong> %s</p>
                                    <p><strong>🚚 Trạng thái đơn hàng:</strong> %s</p>
                                </div>

                                <p>Bạn có thể kiểm tra trạng thái đơn hàng tại:</p>
                                <a href="%s" class="button">Theo dõi đơn hàng</a>

                                <p>Nếu có câu hỏi, vui lòng liên hệ:</p>
                                <p>📞 Hotline: %s <br> 📧 Email: %s</p>

                                <p>Cảm ơn bạn đã tin tưởng và ủng hộ <strong>%s</strong>!</p>
                            </div>
                            <div class="footer">
                                <p>&copy; %s - <a href="%s">Trang web của chúng tôi</a></p>
                            </div>
                        </div>
                    </body>
                    </html>
                """;

        // Danh sách sản phẩm
        List<Map<String, Object>> products = order.getOrderItems()
                .stream()
                .map(item -> Map.<String, Object>of(
                        "name", (Object) item.getName(),
                        "quantity", (Object) item.getQuantity(),
                        "price", (Object) item.getPriceAtOrder(),
                        "total", (Object) (item.getPriceAtOrder() * item.getQuantity())
                ))
                .collect(Collectors.toList());

        StringBuilder productTableHtml = new StringBuilder();
        for (Map<String, Object> product : products) {
            productTableHtml.append("<tr>")
                    .append("<td>")
                    .append(product.get("name"))
                    .append("</td>")
                    .append("<td>")
                    .append(product.get("quantity"))
                    .append("</td>")
                    .append("<td>")
                    .append(String.format("%,.0f VND", product.get("price")))
                    .append("</td>")
                    .append("<td>")
                    .append(String.format("%,.0f VND", product.get("total")))
                    .append("</td>")
                    .append("</tr>");
        }

        // Thay thế dữ liệu vào nội dung HTML
        String formattedContent = String.format(
                content,
                user.getFullName(),
                "BookStore",
                order.getId(),
                order.getCreatedAt(),
                order.getRecipientName() + " - " + order.getRecipientAddress() + " - " + order.getRecipientPhone(),
                productTableHtml.toString(),
                order.getTotalPrice(),
                payment.getPaymentMethod().getDisplayName(),
                payment.getPaymentStatus().getDisplayName(),
                order.getOrderStatus().getDisplayName(),
                "https://yourwebsite.com/track-order/" + order.getId(),
                "093 252 9896",
                "lehuy099@gmail.com",
                "BookStore",
                "BookStore",
                "https://yourwebsite.com"
        );

        mailService.sendMail(user.getEmail(), mailTitle, formattedContent, null);

        log.info("Create order successfully");
    }
}
