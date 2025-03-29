package vn.java.EcommerceWeb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    Optional<Product> findById(Long id);

    boolean existsByNameAndPublisherId(String name, Long publisherId);

    @Query("select p from Product p where " +
            "(:keyword is null or p.name like lower(concat('%', :keyword, '%'))) and" +
            "(:categoryId is null or p.category.id = :categoryId ) and " +
            "(:minPrice is null or p.price >= :minPrice) and " +
            "(:maxPrice is null or p.price <= :maxPrice)")
    Page<Product> searchAndFilterProduct(String keyword, Long categoryId, Double minPrice, Double maxPrice,
                                         Pageable pageable);

//    @Query("select p from Product p where p.")
//    Page<Product> findAllByUserId(Long userId, Pageable pageable);
}
