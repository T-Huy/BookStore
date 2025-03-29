package vn.java.EcommerceWeb.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.java.EcommerceWeb.dto.request.CartItemRequest;
import vn.java.EcommerceWeb.dto.request.OrderRequest;
import vn.java.EcommerceWeb.enums.OrderStatus;
import vn.java.EcommerceWeb.enums.PaymentMethod;
import vn.java.EcommerceWeb.enums.PaymentStatus;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.*;
import vn.java.EcommerceWeb.repository.*;
import vn.java.EcommerceWeb.service.MailService;
import vn.java.EcommerceWeb.service.MomoService;
import vn.java.EcommerceWeb.service.OrderService;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final MailService mailService;
    private final PaymentRepository paymentRepository;
    private final MomoService momoService;

    @Override
    public String createOrder(OrderRequest request) throws MessagingException {
        log.info("Create order starting...");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Long> productIds = request.getCartItems()
                .stream()
                .map(item -> item.getProductId())
                .collect(Collectors.toList());

        List<CartItem> cartItemList = cartItemRepository.findAllByProductIdIn(productIds);
        if (cartItemList.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products are not in cart");
        }

        //Lay toan bo san pham mot lan duy nhat
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        //Kiem tra san pham va stock
        for (CartItemRequest cartItemRequest : request.getCartItems()) {
            Product product = productMap.get(cartItemRequest.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found");
            }
            if (product.getStock() < cartItemRequest.getQuantity()) {
                throw new ResourceNotFoundException("Product" + product.getName() + "out of stock");
            }
        }

        //Tao order
        Order order = Order.builder()
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .recipientAddress(request.getRecipientAddress())
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(request.getCartItems().stream().mapToDouble(item -> {
                    Product product = productMap.get(item.getProductId());
                    return product.getPrice() * item.getQuantity();
                }).sum())
                .user(user)
                .orderItems(new HashSet<>())
                .build();

        //Tao order item va tru stock trong product
        for (CartItemRequest cartItemRequest : request.getCartItems()) {
            Product product = productMap.get(cartItemRequest.getProductId());

            OrderItem orderItem = OrderItem.builder()
                    .image(product.getImage())
                    .name(product.getName())
                    .quantity(cartItemRequest.getQuantity())
                    .priceAtOrder(product.getPrice())
                    .product(product)
                    .order(order)
                    .build();
            order.getOrderItems().add(orderItem);

            product.setStock(product.getStock() - cartItemRequest.getQuantity());


        }
        productRepository.saveAll(productMap.values());

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        order.setPayment(payment);
        orderRepository.save(order);
        log.info("Order created successfully");
        String paymentUrl = null;
        if (request.getPaymentMethod() == PaymentMethod.COD) {
            log.info("Payment with COD starting........");
            payment.setPaymentStatus(PaymentStatus.UNPAID);
            order.setOrderStatus(OrderStatus.PENDING_CONFIRMATION);
            log.info("Payment with COD completed");
            cartItemRepository.deleteByCartIdAndProductIdIn(order.getUser().getCart().getId(), productIds);
            log.info("Cart items deleted");
            //Gửi mail xác nhận
            sendMailConfirm(order, payment);
            orderRepository.save(order);
            log.info("Create order successfully");
            paymentUrl = "Đơn hàng của bạn đã được tạo thành công. Vui lòng kiểm tra email để xác nhận đơn hàng";
        } else if (request.getPaymentMethod() == PaymentMethod.MOMO) {
            log.info("Payment with MOMO starting........");

            String payUrl = momoService.createPaymentUrl(order.getId(), order.getTotalPrice(), "Thanh toán đơn hàng #" + order.getId());
            payment.setPaymentUrl(payUrl);
            orderRepository.save(order);
            log.info("Payment with MOMO has been created payUrl");
            paymentUrl = payUrl;
        }
        return paymentUrl;
    }

    @Override
    public void updateOrderState(Long orderId, String resultCode) {
        log.info("Update order state when payment with momo starting...");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Payment payment = order.getPayment();
        log.warn("OrderID: {}, resultCode: {}", orderId, resultCode);
        if ("0".equals(resultCode)) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
            payment.setPaymentStatus(PaymentStatus.PAID);
            log.info("Payment with momo successfully");

            //Lay danh sach productIds tu orderItems
            List<Long> productIds = order.getOrderItems()
                    .stream()
                    .map(orderItem -> orderItem.getProduct().getId())
                    .toList();

            //Xoa san pham trong cart
            cartItemRepository.deleteByCartIdAndProductIdIn(order.getUser().getCart().getId(), productIds);
            log.info("Cart items deleted after payment with momo successfully");

        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
            payment.setPaymentStatus(PaymentStatus.FAILED);
            log.info("Payment with momo failed, restoring stock..........");

            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
            }
            productRepository.saveAll(order.getOrderItems()
                    .stream()
                    .map(OrderItem::getProduct)
                    .collect(Collectors.toList()));
            log.info("Stock restored for failed payment successfully");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
        log.info("Update order state successfully");
    }


    private void sendMailConfirm(Order order, Payment payment) throws MessagingException {
        String mailTitle = "BookStore - Xác nhận đơn hàng #" + order.getId() + " của bạn thành công";
        // Mẫu HTML
        String content = """
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px; }
                            .container { max-width: 600px; background: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }
                            .header { text-align: center; background: #4CAF50; color: white; padding: 15px; border-radius: 10px 10px 0 0; }
                            .content { padding: 20px; }
                            .order-details { background: #f9f9f9; padding: 15px; border-radius: 8px; }
                            .product-table { width: 100%%; border-collapse: collapse; margin: 10px 0; }
                            .product-table th, .product-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                            .product-table th { background: #4CAF50; color: white; }
                            .total { font-size: 18px; font-weight: bold; color: #e67e22; text-align: right; margin-top: 10px; }
                            .button { display: inline-block; padding: 10px 15px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px; }
                            .footer { text-align: center; padding: 15px; font-size: 14px; color: #555; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>Xác nhận đơn hàng</h2>
                            </div>
                            <div class="content">
                                <p>Xin chào <strong>%s</strong>,</p>
                                <p>Cảm ơn bạn đã đặt hàng tại <strong>%s</strong>! Chúng tôi đã nhận được đơn hàng của bạn và đang xử lý.</p>

                                <div class="order-details">
                                    <p><strong>📌 Mã đơn hàng:</strong> %s</p>
                                    <p><strong>📆 Ngày đặt hàng:</strong> %s</p>
                                    <p><strong>📍 Địa chỉ nhận hàng:</strong> %s</p>
                                    <p><strong>🛒 Sản phẩm đã đặt:</strong></p>
                                    <table class="product-table">
                                        <thead>
                                            <tr>
                                                <th>Tên sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Đơn giá</th>
                                                <th>Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>

                                    <p class="total">💰 Tổng tiền: %,.0f VND</p>
                                    <p><strong>💳 Phương thức thanh toán:</strong> %s</p>
                                    <p><strong>💳 Trạng thái thanh toán:</strong> %s</p>
                                    <p><strong>🚚 Trạng thái đơn hàng:</strong> %s</p>
                                </div>

                                <p>Bạn có thể kiểm tra trạng thái đơn hàng tại:</p>
                                <a href="%s" class="button">Theo dõi đơn hàng</a>

                                <p>Nếu có câu hỏi, vui lòng liên hệ:</p>
                                <p>📞 Hotline: %s <br> 📧 Email: %s</p>

                                <p>Cảm ơn bạn đã tin tưởng và ủng hộ <strong>%s</strong>!</p>
                            </div>
                            <div class="footer">
                                <p>&copy; %s - <a href="%s">Trang web của chúng tôi</a></p>
                            </div>
                        </div>
                    </body>
                    </html>
                """;

        // Danh sách sản phẩm
        List<Map<String, Object>> products = order.getOrderItems()
                .stream()
                .map(item -> Map.<String, Object>of("name", item.getName(), "quantity", item.getQuantity(), "price", item.getPriceAtOrder(), "total", item.getPriceAtOrder() * item.getQuantity()))
                .toList();


        StringBuilder productTableHtml = new StringBuilder();
        for (Map<String, Object> product : products) {
            productTableHtml.append("<tr>")
                    .append("<td>")
                    .append(product.get("name"))
                    .append("</td>")
                    .append("<td>")
                    .append(product.get("quantity"))
                    .append("</td>")
                    .append("<td>")
                    .append(String.format("%,.0f VND", product.get("price")))
                    .append("</td>")
                    .append("<td>")
                    .append(String.format("%,.0f VND", product.get("total")))
                    .append("</td>")
                    .append("</tr>");
        }

        // Thay thế dữ liệu vào nội dung HTML
        String formattedContent = String.format(content, order.getUser()
                .getFullName(), "BookStore", order.getId(), order.getCreatedAt(), order.getRecipientName() + " - " + order.getRecipientAddress() + " - " + order.getRecipientPhone(), productTableHtml, order.getTotalPrice(), payment.getPaymentMethod()
                .getDisplayName(), payment.getPaymentStatus().getDisplayName(), order.getOrderStatus()
                .getDisplayName(), "https://yourwebsite.com/track-order/" + order.getId(), "093 252 9896", "lehuy099@gmail.com", "BookStore", "BookStore", "https://yourwebsite.com");

        mailService.sendMail(order.getUser().getEmail(), mailTitle, formattedContent, null);
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void checkExpiredPaymentOrders() {
        log.info("Checking for expired Momo payments...");

        Date expirationTime = new Date(System.currentTimeMillis() - (20 * 60 * 1000)); //Lấy thời gian 20p

        List<Order> expiredOrders = orderRepository.findByOrderStatusAndUpdatedAtBefore(OrderStatus.PENDING,
                PaymentMethod.MOMO, PaymentStatus.PENDING, expirationTime);
        if(expiredOrders.isEmpty()) {
            log.info("No expired payment found");
            return;
        }
        for (Order order : expiredOrders) {
            log.info("Order {} has expired, restoring stock..........", order.getId());
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.getPayment().setPaymentStatus(PaymentStatus.EXPIRED);

            //Hoan lai stock
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
            }
            productRepository.saveAll(order.getOrderItems()
                    .stream()
                    .map(OrderItem::getProduct)
                    .collect(Collectors.toList()));
            paymentRepository.save(order.getPayment());
            orderRepository.save(order);
            log.info("Order #{} marked as expired and stock restored", order.getId());
            log.info("Stock restored for expired payment successfully");
        }
    }

}
