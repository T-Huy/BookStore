package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.UserHasRole;

import java.util.Set;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    Set<UserHasRole> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
