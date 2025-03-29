package vn.java.EcommerceWeb.service;

import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.PublisherRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.PublisherResponse;

import java.io.IOException;

public interface PublisherService {
    Long createPublisher(PublisherRequest request);

    void updatePublisher(Long id, PublisherRequest request);

    void deletePublisher(Long id);

    PublisherResponse getDetailPublisher(Long id);

    PageResponse<?> getAllPublisher(int page, int size, String sortBy);
}
