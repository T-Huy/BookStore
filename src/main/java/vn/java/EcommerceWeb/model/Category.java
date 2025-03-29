package vn.java.EcommerceWeb.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_category")
public class Category extends AbstractEntity<Long> {

    @Column(name="image")
    private String image;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

}
