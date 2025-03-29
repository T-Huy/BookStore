package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class CategoryRequest implements Serializable {

    @NotBlank(message = "name must be not blank")
    private String name;

    private String description;
}
