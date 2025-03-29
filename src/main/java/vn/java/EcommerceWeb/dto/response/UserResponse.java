package vn.java.EcommerceWeb.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import vn.java.EcommerceWeb.enums.Gender;
import vn.java.EcommerceWeb.enums.UserStatus;

import java.util.Date;

@Getter
@Builder
public class UserResponse {
    private String fullName;

    private String email;

    private String address;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String urlAvatar;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
