package vn.java.EcommerceWeb.service;

import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.CategoryRequest;
import vn.java.EcommerceWeb.dto.response.CategoryResponse;
import vn.java.EcommerceWeb.dto.response.PageResponse;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    Long createCategory(CategoryRequest request, MultipartFile file) throws IOException;

    void updateCategory(Long id,CategoryRequest request, MultipartFile file) throws IOException;

    void deleteCategory(Long id);

    CategoryResponse getDetailCategory(Long id);

    PageResponse<List<CategoryResponse>> getAllCategory(int pageNo, int pageSize, String sortBy);
}
