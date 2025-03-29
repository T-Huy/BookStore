package vn.java.EcommerceWeb.service;

import jakarta.mail.MessagingException;
import vn.java.EcommerceWeb.dto.request.OrderRequest;

import java.io.UnsupportedEncodingException;

public interface OrderService {

    String createOrder(OrderRequest request) throws MessagingException, UnsupportedEncodingException;

    void updateOrderState(Long orderId, String resultCode);

    void checkExpiredPaymentOrders();


}
