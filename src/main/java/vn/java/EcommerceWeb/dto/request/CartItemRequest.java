package vn.java.EcommerceWeb.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CartItemRequest implements Serializable {

    private Long productId;

    private Integer quantity;
}
