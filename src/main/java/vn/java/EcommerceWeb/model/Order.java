package vn.java.EcommerceWeb.model;

import jakarta.persistence.*;
import lombok.*;
import vn.java.EcommerceWeb.enums.OrderStatus;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_order")
public class Order extends  AbstractEntity<Long> {

    @Column(name="recipient_name")
    private String recipientName;

    @Column(name="recipient_phone")
    private String recipientPhone;

    @Column(name="recipient_address")
    private String recipientAddress;

    @Column(name="total_price")
    private Double totalPrice;

    @Column(name="order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
