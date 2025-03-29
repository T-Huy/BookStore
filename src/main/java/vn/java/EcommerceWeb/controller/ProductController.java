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
import vn.java.EcommerceWeb.dto.request.ProductRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ProductResponse;
import vn.java.EcommerceWeb.dto.response.ResponseData;
import vn.java.EcommerceWeb.dto.response.ResponseError;
import vn.java.EcommerceWeb.service.ProductService;

@RestController
@RequestMapping("/v1/api/product")
@Validated
@Slf4j
@Tag(name = "Product Controller")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create product")
    @PostMapping("/")
    public ResponseData<?> createProduct(@RequestPart @Valid ProductRequest request,
                                         @RequestPart(value = "file", required = true) MultipartFile file) {
        try {
            Long productId = productService.createProduct(request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Create product successfully", productId);
        } catch (Exception e) {
            log.error("Create product error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create product failed");
        }
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    public ResponseData<?> updateProduct(@PathVariable Long id, @RequestPart @Valid ProductRequest request,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            productService.updateProduct(id, request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Update product successfully");
        } catch (Exception e) {
            log.error("Update product error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update product failed");
        }
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete product successfully");
        } catch (Exception e) {
            log.error("Delete product error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete product failed");
        }
    }

    @Operation(summary = "Get detail product")
    @GetMapping("/{id}")
    public ResponseData<?> getDetailProduct(@PathVariable Long id) {
        try {
            ProductResponse productResponse = productService.getDetailProduct(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Get detail product successfully", productResponse);
        } catch (Exception e) {
            log.error("Get detail product error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get detail product failed");
        }
    }

    @Operation(summary = "Get all product")
    @GetMapping("/")
    public ResponseData<?> getAllProduct(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) Double minPrice,
                                         @RequestParam(required = false) Double maxPrice,
                                         @RequestParam(defaultValue = "0",required = false) int pageNo,
                                         @RequestParam(defaultValue = "20", required = false) int pageSize,
                                         @RequestParam(defaultValue = "updatedAt:ASC", required = false) String sortBy) {
        try {
            PageResponse<?> listProductResponse = productService.getAllProduct(keyword, categoryId, minPrice, maxPrice,
                    pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "Get all product successfully", listProductResponse);
        } catch (Exception e) {
            log.error("Get all product error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all product failed");
        }
    }
}
