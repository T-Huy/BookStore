package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.java.EcommerceWeb.enums.OrderStatus;
import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.enums.PaymentStatus;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.CartItem;
import vn.java.EcommerceWeb.model.Order;
import vn.java.EcommerceWeb.model.Payment;
import vn.java.EcommerceWeb.repository.CartItemRepository;
import vn.java.EcommerceWeb.repository.CartRepository;
import vn.java.EcommerceWeb.repository.PaymentRepository;
import vn.java.EcommerceWeb.service.PaymentService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Payment processPayment(Order order,List<CartItem> cartItemList ,PaymentMethod paymentMethod) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .build();

        if (paymentMethod == PaymentMethod.COD) {
            log.info("Payment with COD starting........");
            payment.setPaymentStatus(PaymentStatus.UNPAID);
            order.setOrderStatus(OrderStatus.PENDING_CONFIRMATION);
            log.info("Payment with COD completed");
            cartItemRepository.deleteByCartIdAndProductIdIn(
                    order.getUser().getCart().getId(),
                    cartItemList.stream()
                            .map(CartItem::getProduct)
                            .map(product -> product.getId())
                            .toList());
            log.info("Cart items deleted");
        } else if (paymentMethod == PaymentMethod.MOMO) {
            log.info("Payment with MOMO starting........");
            payment.setTransactionId("MOMO_" + System.currentTimeMillis());
            payment.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            log.info("Payment with MOMO completed");
        }
        return paymentRepository.save(payment);
    }
}
