package vn.java.EcommerceWeb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
