package vn.java.EcommerceWeb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("Đang xử lý"),
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    FAILED("Thất bại"),
    EXPIRED("Hết thời gian thanh toán"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;
}
