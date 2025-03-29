package vn.java.EcommerceWeb.service;

import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.ProductRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ProductResponse;

import java.io.IOException;

public interface ProductService {

    Long createProduct(ProductRequest request, MultipartFile file) throws IOException;

    void updateProduct(Long id, ProductRequest request, MultipartFile file) throws IOException;

    void deleteProduct(Long id);

    ProductResponse getDetailProduct(Long id);

    PageResponse<?> getAllProduct(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllProduct(String keyword, Long categoryId, Double minPrice, Double maxPrice, int pageNo,
                                  int pageSize, String sortBy);
}
