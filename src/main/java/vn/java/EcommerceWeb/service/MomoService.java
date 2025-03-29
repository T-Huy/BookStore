package vn.java.EcommerceWeb.service;

public interface MomoService {

    String createPaymentUrl(Long orderId, Double amount, String orderInfo);
}
