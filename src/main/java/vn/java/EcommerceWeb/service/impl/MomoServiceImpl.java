package vn.java.EcommerceWeb.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.java.EcommerceWeb.service.MomoService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoServiceImpl implements MomoService {

    @Value("${momo.partner-code}")
    private String partnerCode;
    @Value("${momo.access-key}")
    private String accessKey;
    @Value("${momo.secret-key}")
    private String secretKey;
    @Value("${momo.return-url}")
    private String returnUrl;
    @Value("${momo.ipn-url}")
    private String ipnUrl;
    @Value("${momo.request-type}")
    private String requestType;
    @Value("${momo.endpoint}")
    private String endPoint;

    @Override
    public String createPaymentUrl(Long orderID, Double amount, String orderInfo) {
        //Tao requestId va orderId
        long currentTime = System.currentTimeMillis();
        String requestId = orderID + "_" + currentTime;
        String orderId = orderID + "_" + currentTime;

        //Xay dung rawSignature
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + String.valueOf(amount.intValue()) +
                "&extraData=" + "" +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        //Ky rawSignature bang HMAC SHA256
        String signature = hmacSHA256(rawSignature, secretKey);

        //Xay dung requestBody
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("partnerName", "Payment Momo");
        requestBody.put("storeId", "MomoTestStore");
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount.intValue());
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", returnUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("lang", "vi");
        requestBody.put("requestType", requestType);
        requestBody.put("autoCapture", true);
        requestBody.put("extraData", "");
        requestBody.put("orderGroupId", "");
        requestBody.put("signature", signature);

        //Gui yeu cau HTTP POST toi Momo API
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String response = restTemplate.postForObject(
                    endPoint,
                    requestBody,
                    String.class
            );
            //Parse response tu JSON de lay payUrl
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            log.info("Response from Momo: {}", responseMap);
            log.info("Response from Momo: payUrl: {}, ipnUrl: {}", responseMap.get("payUrl"), responseMap.get("ipnUrl"));
            return (String) responseMap.get("payUrl");
        }
        catch (Exception e) {
            log.error("Error while creating payment URL", e);
        }
        return "Thanh toán với momo ko thành công, ko thể gửi request";
    }

    private String hmacSHA256(String rawSignature, String secretKey) {
        try {
            // Tạo key từ secretKey
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");

            // Khởi tạo HMAC với thuật toán SHA-256
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            // Cập nhật dữ liệu (rawSignature) và tính toán chữ ký
            byte[] hash = mac.doFinal(rawSignature.getBytes());

            // Chuyển hash sang dạng Hex hoặc Base64
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating HMAC SHA-256 signature", e);
        }
    }


}
