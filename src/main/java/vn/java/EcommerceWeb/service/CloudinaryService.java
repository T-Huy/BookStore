package vn.java.EcommerceWeb.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    public String uploadFile(MultipartFile file) throws IOException;

    public void deleteFile(String publicId) throws IOException;

    public String uploadVideo(MultipartFile file) throws IOException;
}
