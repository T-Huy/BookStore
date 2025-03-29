package vn.java.EcommerceWeb.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartProductResponse {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private String image;
    private String category;
    private Double totalPrice;
}
