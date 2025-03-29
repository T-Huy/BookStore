package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role r inner join UserHasRole ur on r.id = ur.role.id where ur.user.id = ?1")
    List<Role> getAllRolesByUserId(Long userId);

    Optional<Role> findByName(String name);
    boolean existsByName(String name);


}
