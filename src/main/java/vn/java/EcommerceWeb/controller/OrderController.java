package vn.java.EcommerceWeb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            String paymentUrl = orderService.createOrder(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Create order successfully", paymentUrl);
        } catch (Exception e) {
            log.error("Create order error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Momo return order")
    @PostMapping("/momo-return")
    public ResponseData<?> momoReturn(@RequestParam String orderId, @RequestParam String resultCode) {
        try {
            log.warn("Callback momo: orderID: {}, resultCode: {}", orderId, resultCode);
            String orderID = orderId.split("_")[0];
            orderService.updateOrderState(Long.parseLong(orderID), resultCode);
            return new ResponseData<>(HttpStatus.OK.value(), "Callback momo successfully");
        } catch (Exception e) {
            log.error("Callback momo: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
