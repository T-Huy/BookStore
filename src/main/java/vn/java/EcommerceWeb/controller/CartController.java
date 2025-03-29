package vn.java.EcommerceWeb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.java.EcommerceWeb.dto.request.AddToCartRequest;
import vn.java.EcommerceWeb.dto.request.RemoveCartRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ResponseData;
import vn.java.EcommerceWeb.dto.response.ResponseError;
import vn.java.EcommerceWeb.service.CartService;

@RestController
@RequestMapping("/v1/api/cart")
@Validated
@Slf4j
@Tag(name = "Cart Controller")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add Product to Cart")
    @PostMapping("/")
    public ResponseData<?> addProductToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            cartService.addProductToCart(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Add product to cart successfully");
        } catch (Exception e) {
            log.error("Add product to cart error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add product to cart failed");
        }
    }

    @Operation(summary = "Remove Product to Cart")
    @DeleteMapping("/")
    public ResponseData<?> addProductToCart(@Valid @RequestBody RemoveCartRequest request) {
        try {
            cartService.removeProductToCart(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Remove product to cart successfully");
        } catch (Exception e) {
            log.error("Remove product to cart error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Remove product to cart failed");
        }
    }

    @Operation(summary = "Get Product in Cart")
    @GetMapping("/{userId}/products")
    public ResponseData<?> getAllProductInCart(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0",required = false) int pageNo,
                                               @RequestParam(defaultValue = "20", required = false) int pageSize,
                                               @RequestParam(defaultValue = "updatedAt:ASC", required = false) String sortBy) {
        try {
            PageResponse<?>  listProductInCart = cartService.getAllProductInCart(userId, pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "Get product in cart successfully", listProductInCart);
        } catch (Exception e) {
            log.error("Get product in cart error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get product in cart failed");
        }
    }
}
