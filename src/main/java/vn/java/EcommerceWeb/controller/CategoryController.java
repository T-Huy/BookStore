package vn.java.EcommerceWeb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.CategoryRequest;
import vn.java.EcommerceWeb.dto.response.CategoryResponse;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ResponseData;
import vn.java.EcommerceWeb.dto.response.ResponseError;
import vn.java.EcommerceWeb.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/category")
@Validated
@Slf4j
@Tag(name = "Category Controller")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category")
    @PostMapping("/")
    public ResponseData<?> createCategory(
            @RequestPart @Valid CategoryRequest request, @RequestPart(value = "file",
                                                                      required = true) MultipartFile file) {
        try {
            Long categoryId = categoryService.createCategory(request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Create category successfully", categoryId);
        } catch (Exception e) {
            log.error("Create category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create category failed");
        }
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseData<?> updateCategory(
            @PathVariable Long id,
            @RequestPart @Valid CategoryRequest request,
            @RequestPart(value = "file", required = true) MultipartFile file) {
        try {
            log.info("Update category with id: {}", id);
            log.info("Request: {}", request);
            log.info("File: {}", file);
            categoryService.updateCategory(id, request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Update category successfully");
        } catch (Exception e) {
            log.error("Update category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update category failed");
        }
    }

    @Operation(summary = "Update category")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete category successfully");
        } catch (Exception e) {
            log.error("Delete category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete category failed");
        }
    }

    @Operation(summary = "Get detail category")
    @GetMapping("/{id}")
    public ResponseData<CategoryResponse> getDetailCategory(@PathVariable Long id) {
        try {
            CategoryResponse categoryResponse = categoryService.getDetailCategory(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Get detail category successfully", categoryResponse);
        } catch (Exception e) {
            log.error("Get detail category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get detail category failed");
        }
    }

    @Operation(summary = "Get all category")
    @GetMapping("/")
    public ResponseData<?> getAllCategory(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                             @RequestParam(defaultValue = "20", required = false) int pageSize,
                                             @RequestParam(defaultValue = "updatedAt:ASC", required = false) String sortBy) {
        try {
            PageResponse<List<CategoryResponse>> listCategory = categoryService.getAllCategory(pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "Get all category successfully", listCategory);
        } catch (Exception e) {
            log.error("Get all category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all category failed");
        }
    }
}
