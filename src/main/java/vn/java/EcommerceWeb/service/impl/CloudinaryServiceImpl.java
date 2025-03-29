package vn.java.EcommerceWeb.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileNameWithoutExt = FilenameUtils.getBaseName(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            String uniqueFileName = fileNameWithoutExt + "_" + timestamp;

            Map uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap("folder", "/EcommerceWeb", "public_id", uniqueFileName, "use_filename", "true", "unique_filename", "false", "resource_type", "auto"));
            log.info("Upload file to Cloudinary successfully");
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            log.error("Fail to upload file to Cloudinary", e);
            throw new IOException("Fail to upload file to Cloudinary");
        }
    }

    @Override
    public void deleteFile(String imageUrl) throws IOException {
        try {
            Pattern pattern = Pattern.compile("/upload/(?:v\\d+/)?(.+?)\\.");
            Matcher matcher = pattern.matcher(imageUrl);
            if (matcher.find()) {
                String publicId = matcher.group(1);
                log.info("Public ID: " + publicId);
                Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Delete file from Cloudinary successfully");
            } else {
                log.warn("Could not extract publicId from URL: " + imageUrl);
            }
        } catch (IOException e) {
            log.error("Fail to delete file from Cloudinary", e);
            throw new IOException("Fail to delete file");
        }
    }


    @Override
    public String uploadVideo(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader()
                    .uploadLarge(file.getBytes(), ObjectUtils.asMap("resource_type", "video", "chunk_size", 6000000));
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            log.error("Fail to upload video to Cloudinary", e);
            throw new IOException("Fail to upload video to Cloudinary");
        }
    }
}
