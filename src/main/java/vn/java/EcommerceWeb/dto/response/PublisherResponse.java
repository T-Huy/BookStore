package vn.java.EcommerceWeb.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class PublisherResponse implements Serializable {

        private Long id;

        private String name;

        private String email;

        private String phone;

        private String address;

        private String description;
}
