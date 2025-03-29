package vn.java.EcommerceWeb.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.enums.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
public class UserRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotBlank(message = "email must be not blank")
    @Email(message = "email must be in the correct format")
    private String email;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotBlank(message = "address must be not blank")
    private String address;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @EnumPattern(name = "gender", regexp = "MALE|FEMALE")
    private Gender gender;

    @PhoneNumber
    private String phone;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    private Set<String> userRoles;

}
