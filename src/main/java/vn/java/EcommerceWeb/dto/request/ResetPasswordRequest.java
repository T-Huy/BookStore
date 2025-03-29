package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResetPasswordRequest implements Serializable {

    @NotBlank(message = "Secret key can not be blank")
    private String secretKey;
    @NotBlank(message = "New password can not be blank")
    private String newPassword;
    @NotBlank(message = "Confirm password can not be blank")
    private String confirmPassword;
}
