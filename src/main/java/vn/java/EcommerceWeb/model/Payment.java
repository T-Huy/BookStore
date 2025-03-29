package vn.java.EcommerceWeb.model;

import jakarta.persistence.*;
import lombok.*;
import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.enums.PaymentStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_payment")
public class Payment extends AbstractEntity<Long> {

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name="payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name="payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name="payment_url")
    private String paymentUrl;

}
