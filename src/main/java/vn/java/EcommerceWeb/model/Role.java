package vn.java.EcommerceWeb.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Role")
@Table(name = "tbl_role")
public class Role extends AbstractEntity<Integer> {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role",cascade= CascadeType.ALL)
    private Set<UserHasRole> userHasRoles;


}
