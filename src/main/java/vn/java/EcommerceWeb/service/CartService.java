package vn.java.EcommerceWeb.service;

import org.springframework.data.domain.Pageable;
import vn.java.EcommerceWeb.dto.request.AddToCartRequest;
import vn.java.EcommerceWeb.dto.request.RemoveCartRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;

public interface CartService {

    void addProductToCart(AddToCartRequest request);

    void removeProductToCart(RemoveCartRequest request);

    PageResponse<?> getAllProductInCart(Long userId, int pageNo, int pageSize, String sortBy);
}
