package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class AddToCartRequest implements Serializable {

    @NotNull(message = "userId must be not null")
    private Long userId;

    @NotNull(message = "productId must be not null")
    private Long productId;

    @Min(value = 1, message = "quantity must be greater than 0")
    private Integer quantity;
}
