package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePassword {
    @NotBlank(message = "Old password must be not blank")
    private String oldPassword;
    @NotBlank(message = "New password must be not blank")
    private String newPassword;
    @NotBlank(message = "Confirm password must be not blank")
    private String confirmPassword;
}
