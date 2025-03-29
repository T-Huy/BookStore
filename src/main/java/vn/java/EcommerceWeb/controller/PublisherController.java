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
import vn.java.EcommerceWeb.dto.request.PublisherRequest;
import vn.java.EcommerceWeb.dto.response.*;
import vn.java.EcommerceWeb.service.CategoryService;
import vn.java.EcommerceWeb.service.PublisherService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/publisher")
@Validated
@Slf4j
@Tag(name = "Publisher Controller")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @Operation(summary = "Create publisher")
    @PostMapping("/")
    public ResponseData<?> createPublisher(@RequestBody @Valid PublisherRequest request) {
        try {
            Long publisherId = publisherService.createPublisher(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Create publisher successfully", publisherId);
        } catch (Exception e) {
            log.error("Create publisher error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create publisher failed");
        }
    }

    @Operation(summary = "Update publisher")
    @PutMapping("/{id}")
    public ResponseData<?> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid PublisherRequest request) {
        try {
            publisherService.updatePublisher(id, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update publisher successfully");
        } catch (Exception e) {
            log.error("Update publisher error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update publisher failed");
        }
    }

    @Operation(summary = "Update category")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteCategory(@PathVariable Long id) {
        try {
            publisherService.deletePublisher(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete publisher successfully");
        } catch (Exception e) {
            log.error("Delete publisher error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete publisher failed");
        }
    }

    @Operation(summary = "Get detail publisher")
    @GetMapping("/{id}")
    public ResponseData<PublisherResponse> getDetailPublisher(@PathVariable Long id) {
        try {
            PublisherResponse publisherResponse = publisherService.getDetailPublisher(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Get detail publisher successfully", publisherResponse);
        } catch (Exception e) {
            log.error("Get detail category error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get detail publisher failed");
        }
    }

    @Operation(summary = "Get all publisher")
    @GetMapping("/")
    public ResponseData<?> getAllPublisher(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                             @RequestParam(defaultValue = "20", required = false) int pageSize,
                                             @RequestParam(defaultValue = "updatedAt:ASC", required = false) String sortBy) {
        try {
            PageResponse<?> listPublisher = publisherService.getAllPublisher(pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "Get all publisher successfully", listPublisher);
        } catch (Exception e) {
            log.error("Get all publisher error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all publisher failed");
        }
    }
}
