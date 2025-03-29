package vn.java.EcommerceWeb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    MOMO("Ví điện tử Momo"),
    ZALOPAY("Ví điện tử ZaloPay"),
    VISA("Thẻ Visa"),
    MASTER_CARD("Thẻ Master Card");

    private final String displayName;
}
