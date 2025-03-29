package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.dto.request.PublisherRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.PublisherResponse;
import vn.java.EcommerceWeb.model.Publisher;

import java.util.Optional;

@Repository
public interface PublisherRepository  extends JpaRepository<Publisher, Long> {

    boolean existsByName(String name);

    Optional<Publisher> findById(Long id);
}
