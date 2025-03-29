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
import vn.java.EcommerceWeb.dto.request.OrderRequest;
import vn.java.EcommerceWeb.dto.response.ResponseData;
import vn.java.EcommerceWeb.dto.response.ResponseError;
import vn.java.EcommerceWeb.service.OrderService;

@RestController
@RequestMapping("/v1/api/order")
@Validated
@Slf4j
@Tag(name = "Order Controller")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order")
    @PostMapping("/")
    public ResponseData<?> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            orderService.createOrder(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Create order successfully");
        } catch (Exception e) {
            log.error("Create order error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
