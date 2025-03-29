package vn.java.EcommerceWeb.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String description;
    private String image;
    private String category;
    private String publisher;
    private List<String> authors;

}
