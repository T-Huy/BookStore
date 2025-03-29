package vn.java.EcommerceWeb.dto.request;

import lombok.Getter;
import vn.java.EcommerceWeb.enums.OrderStatus;
import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.enums.PaymentStatus;
import vn.java.EcommerceWeb.model.CartItem;

import java.io.Serializable;
import java.util.List;

@Getter
public class OrderRequest implements Serializable {

    private Long userId;

    private List<CartItemRequest> cartItems;

    private String recipientName;

    private String recipientPhone;

    private String recipientAddress;

    private PaymentMethod paymentMethod;


}
