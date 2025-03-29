package vn.java.EcommerceWeb.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.java.EcommerceWeb.service.MailService;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.from}") private String emailFrom;


    @Override
    public String sendMail(String recipients, String subject, String content, MultipartFile[] files) throws MessagingException {
        log.info("Sending ...");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailFrom);
        if (recipients.contains(",")) {
            helper.setTo(InternetAddress.parse(recipients));
        } else {
            helper.setTo(recipients);
        }
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
        log.info("Email sent successfully, to: {}", recipients);
        return "sent";
    }

    @Override
    public void sendConfirmLink(String emailTo, Long userId, String secretCode) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirm link to {}", emailTo);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        String linkConfirm = String.format("http://localhost:80/api/user/confirm/%s?secretCode=%s", userId, secretCode);
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);
        helper.setFrom(emailFrom, "Lê Tấn Huy");
        helper.setTo(emailTo);
        helper.setSubject("Confirm your account");
        String html = springTemplateEngine.process("confirm-email.html", context);
        helper.setText(html, true);
        mailSender.send(message);
        log.info("Email sent successfully sendConfirmLink, to: {}", emailTo);
    }

    @Override
    public void sendMailForm(String recipients, String subject, String templateHtml, Map<String, Object> placeholders
            , List< Map<String, String>> tableData, List<String> tableHeaders) throws MessagingException, UnsupportedEncodingException {
        log.info("Preparing........ to send email to: {}", recipients);

        //Thay the thong tin co dinh chung
        String content = templateHtml;

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }

        //Thay the thong tin cho bang
        StringBuilder table = new StringBuilder();

        //Tao header cho bang
        table.append("<table class='product-table'><thead><tr>");
        for (String header : tableHeaders) {
            table.append("<th>").append(header).append("</th>");
        }
        table.append("</tr></thead><tbody>");

        //Then du lieu cho bang
        for (Map<String, String> row : tableData) {
            table.append("<tr>");
            for (String header : tableHeaders) {
                table.append("<td>").append(row.getOrDefault(header,"")).append("</td>");
            }
            table.append("</tr>");
        }
        table.append("</tbody></table>");
        content = content.replace("{{product_table}}", table.toString());

        //Gui mail
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setFrom(emailFrom, "BookStore");
        helper.setTo(InternetAddress.parse(recipients));
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
        log.info("Email sent successfully sendMailForm, to: {}", recipients);
    }
}
