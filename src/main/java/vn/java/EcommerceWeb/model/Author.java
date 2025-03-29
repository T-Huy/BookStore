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
@Table(name = "tbl_author")
public class Author extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "biography ", columnDefinition = "TEXT")
    private String biography;

    @OneToMany(mappedBy = "author",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    private Set<ProductAuthor> productAuthors = new HashSet<>();
}
