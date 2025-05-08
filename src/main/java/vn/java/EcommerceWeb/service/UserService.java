package vn.java.EcommerceWeb.service;

import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.UpdatePassword;
import vn.java.EcommerceWeb.dto.request.UserRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.UserDetailReponse;
import vn.java.EcommerceWeb.enums.UserStatus;
import vn.java.EcommerceWeb.model.User;

import java.io.IOException;

public interface UserService {

    UserDetailReponse getCurrentUserDetail();

    long createUser(UserRequest request, MultipartFile file) throws IOException;

    void updateUser(Long id, UserRequest request, MultipartFile file) throws IOException;

    PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy);

    void deleteUser(Long userId);

    UserDetailReponse getDetailUserById(Long userId);

    void updatePassword(Long userId, UpdatePassword request);

    User getByEmail(String email);

    long saveUser(User user);

    void changeStatus(Long userId, UserStatus status);

    void confirmUser(Long userId, String secretCode);

}
