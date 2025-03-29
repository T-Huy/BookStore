package vn.java.EcommerceWeb.service;

import jakarta.mail.MessagingException;
import vn.java.EcommerceWeb.dto.request.OrderRequest;

import java.io.UnsupportedEncodingException;

public interface OrderService {

    void createOrder(OrderRequest request) throws MessagingException, UnsupportedEncodingException;
}
