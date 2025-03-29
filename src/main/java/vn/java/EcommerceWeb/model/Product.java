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
@Table(name = "tbl_product")
public class Product extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name="stock")
    private Integer stock;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    private Set<ProductAuthor> productAuthors = new HashSet<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();



}
