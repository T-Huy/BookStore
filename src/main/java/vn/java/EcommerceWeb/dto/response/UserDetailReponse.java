package vn.java.EcommerceWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import vn.java.EcommerceWeb.enums.EnumPattern;
import vn.java.EcommerceWeb.enums.Gender;
import vn.java.EcommerceWeb.enums.PhoneNumber;
import vn.java.EcommerceWeb.enums.UserStatus;

import java.util.Date;
import java.util.Set;

@Getter
@Builder
public class UserDetailReponse {

    private Long id;

    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotBlank(message = "email must be not blank")
    @Email(message = "email must be in the correct format")
    private String email;

    @NotBlank(message = "address must be not blank")
    private String address;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @EnumPattern(name = "gender", regexp = "MALE|FEMALE")
    private Gender gender;

    @NotBlank(message = "urlAvatar must be not blank")
    private String urlAvatar;

    @PhoneNumber
    private String phone;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    private Set<String> userRoles;
}
