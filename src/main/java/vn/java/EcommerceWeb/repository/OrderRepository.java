package vn.java.EcommerceWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.java.EcommerceWeb.enums.OrderStatus;
import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.enums.PaymentStatus;
import vn.java.EcommerceWeb.model.Order;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.orderStatus = ?1 and o.payment.paymentMethod = ?2 and o.payment" + ".paymentStatus = ?3 and o.updatedAt < ?4")
    List<Order> findByOrderStatusAndUpdatedAtBefore(OrderStatus orderStatus, PaymentMethod paymentMethod, PaymentStatus paymentStatus, Date time);
}
