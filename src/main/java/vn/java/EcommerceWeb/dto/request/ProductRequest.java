package vn.java.EcommerceWeb.dto.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.java.EcommerceWeb.model.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ProductRequest implements Serializable {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Price cannot be null")
    private Double price;

    @NotNull(message = "Stock cannot be null")
    private Integer stock;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "CategoryId must not be null")
    @Min(value = 1, message = "CategoryId must be greater than 0")
    private Long categoryId;

    @NotNull(message = "CategoryId must not be null")
    @Min(value = 1, message = "publisherId must be greater than 0")
    private Long publisherId;


    private List<Long> authorIds;



}
