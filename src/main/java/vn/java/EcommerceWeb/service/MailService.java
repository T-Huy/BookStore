package vn.java.EcommerceWeb.service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface MailService {
    String sendMail(String recipients, String subject, String content, MultipartFile[] files) throws MessagingException;

    void sendConfirmLink(String emailTo, Long userId, String secretCode) throws MessagingException, UnsupportedEncodingException;

    void sendMailForm(String recipients, String subject, String templateHtml, Map<String, Object> placeholders
            , List< Map<String, String>> tableData, List<String> tableHeaders) throws MessagingException, UnsupportedEncodingException;

}
