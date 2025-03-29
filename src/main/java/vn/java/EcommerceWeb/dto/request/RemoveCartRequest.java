package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class RemoveCartRequest implements Serializable {

    @NotNull(message = "userId must be not null")
    @Min(value = 1, message = "userId must be greater than 0")
    private Long userId;

    @NotNull(message = "productIds must be not null")
    private List<Long> productIds;

}
