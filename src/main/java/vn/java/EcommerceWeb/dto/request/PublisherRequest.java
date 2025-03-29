package vn.java.EcommerceWeb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.java.EcommerceWeb.enums.PhoneNumber;

import java.io.Serializable;

@Getter
public class PublisherRequest implements Serializable {

    @NotBlank(message = "Name must be not blank")
    private String name;

    @NotBlank(message = "Email must be not blank")
    @Email(message = "Email must be in the correct format")
    private String email;

    @PhoneNumber
    private String phone;

    @NotBlank(message = "Address must be not blank")
    private String address;

    private String description;
}
