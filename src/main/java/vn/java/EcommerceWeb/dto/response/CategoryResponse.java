package vn.java.EcommerceWeb.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class CategoryResponse implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String image;
}
