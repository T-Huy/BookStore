package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.CategoryRequest;
import vn.java.EcommerceWeb.dto.response.CategoryResponse;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.Category;
import vn.java.EcommerceWeb.repository.CategoryRepository;
import vn.java.EcommerceWeb.service.CategoryService;
import vn.java.EcommerceWeb.service.CloudinaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Long createCategory(CategoryRequest request, MultipartFile file) throws IOException {
        log.info("Category start creating");
        if (categoryRepository.existsByName(request.getName())) {
            log.error("Category already exists {}", request.getName());
            throw new ResourceNotFoundException("Category already exists");
        }
        String imageUrl = cloudinaryService.uploadFile(file);

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(imageUrl)
                .build();
        categoryRepository.save(category);
        log.info("Category created successfully");
        return category.getId();
    }

    @Override
    public void updateCategory(Long id, CategoryRequest request, MultipartFile file) throws IOException {
        log.info("Category start updating");
        Category category = getCategoryById(id);
        String imageUrl = category.getImage();
        if(!StringUtils.isEmpty(file)) {
            cloudinaryService.deleteFile(imageUrl);
            imageUrl = cloudinaryService.uploadFile(file);
            category.setImage(imageUrl);
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        log.info("Category updated successfully");
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Category start deleting");
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        log.info("Category deleted successfully");
    }

    @Override
    public CategoryResponse getDetailCategory(Long id) {
        log.info("Category start getting detail");
        Category category = getCategoryById(id);
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage())
                .build();
        log.info("Category get detail successfully");
        return categoryResponse;
    }

    @Override
    public PageResponse<List<CategoryResponse>> getAllCategory(int pageNo, int pageSize, String sortBy) {
        if(pageNo > 0) {
            pageNo = pageNo -1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase(("asc"))) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponse> responses = categories.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .image(category.getImage())
                        .build())
                .collect(Collectors.toList());
        log.info("Get all category successfully");
        return PageResponse.<List<CategoryResponse>>builder()
                .pageNo(pageNo+1)
                .pageSize(pageSize)
                .totalPage(categories.getTotalPages())
                .totalElement(categories.getTotalElements())
                .items(responses)
                .build();
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
