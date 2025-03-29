package vn.java.EcommerceWeb.service;

import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.model.CartItem;
import vn.java.EcommerceWeb.model.Order;
import vn.java.EcommerceWeb.model.Payment;

import java.util.List;

public interface PaymentService {

    Payment processPayment(Order order, List<CartItem> cartItemList, PaymentMethod paymentMethod);
}
