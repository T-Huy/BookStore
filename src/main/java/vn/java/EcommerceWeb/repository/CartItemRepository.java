package vn.java.EcommerceWeb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.CartItem;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Page<CartItem> findAllByCartId(Long cartId, Pageable pageable);

    void deleteAllByCartId(Long cartId);

    void deleteByCartIdAndProductIdIn(Long cartId, List<Long> productIds);

    List<CartItem> findAllByProductIdIn(List<Long> productIds);
}
