package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.UpdatePassword;
import vn.java.EcommerceWeb.dto.request.UserRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.UserDetailReponse;
import vn.java.EcommerceWeb.enums.UserStatus;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.Role;
import vn.java.EcommerceWeb.model.User;
import vn.java.EcommerceWeb.model.UserHasRole;
import vn.java.EcommerceWeb.repository.RoleRepository;
import vn.java.EcommerceWeb.repository.UserHasRoleRepository;
import vn.java.EcommerceWeb.repository.UserRepository;
import vn.java.EcommerceWeb.service.CloudinaryService;
import vn.java.EcommerceWeb.service.MailService;
import vn.java.EcommerceWeb.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final UserHasRoleRepository userHasRoleRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserDetailReponse getCurrentUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User currentUser)) {
            log.error("User not login");
            throw new ResourceNotFoundException("User not login");
        }
        log.info("User get current user detail successfully");
        return UserDetailReponse.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .address(currentUser.getAddress())
                .dateOfBirth(currentUser.getDateOfBirth())
                .gender(currentUser.getGender())
                .urlAvatar(currentUser.getUrlAvatar())
                .phone(currentUser.getPhone())
                .status(currentUser.getStatus())
                .userRoles(currentUser.getRoles()
                        .stream()
                        .map(userHasRole -> userHasRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    @Transactional
    public long createUser(UserRequest request, MultipartFile file) throws IOException {

        log.info("User start creating");
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists {}", request.getEmail());
            throw new ResourceNotFoundException("User already exists");
        }
        String imageUrl = cloudinaryService.uploadFile(file);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .urlAvatar(imageUrl)
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .build();
        Set<UserHasRole> newRoles = request.getUserRoles().stream().map(userRole -> {
            Role role = roleRepository.findByName(userRole)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            return UserHasRole.builder().user(user).role(role).build();
        }).collect(Collectors.toSet());
        user.setRoles(newRoles);
        userRepository.save(user);
        log.info("User created successfully");
        return user.getId();
    }

    @Override
    public void updateUser(Long id, UserRequest request, MultipartFile file) throws IOException {
        log.info("User start updating");
        User user = getUserById(id);

        String imageUrl = cloudinaryService.uploadFile(file);

        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setUrlAvatar(imageUrl);
        user.setPhone(request.getPhone());


//        Set<UserHasRole> newRoles = request.getRoles().stream()
//                .map(roleName -> {
//                    Role role = roleRepository.findByName(roleName)
//                            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
//                    return UserHasRole.builder().user(user).role(role).build();
//                })
//                .collect(Collectors.toSet());
//        user.setRoles(newRoles);

        //Chay dc
        Set<UserHasRole> existingRoles = user.getRoles();
        Set<UserHasRole> newRoles = request.getUserRoles().stream().map(userRole -> {
            Role role = roleRepository.findByName(userRole)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            return UserHasRole.builder().user(user).role(role).build();
        }).collect(Collectors.toSet());

        existingRoles.clear(); // Xóa roles cũ
        existingRoles.addAll(newRoles); // Thêm roles mới vào danh sách cũ

        userRepository.save(user);


        log.info("User updated successfully");
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    @Override
    public long saveUser(User user) {
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public void changeStatus(Long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User status changed successfully");
    }

    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User deleted successfully, userId={}", userId);
    }

    @Override
    public UserDetailReponse getDetailUserById(Long userId) {
        User user = getUserById(userId);
        log.info("User get detail successfully");
        return UserDetailReponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .urlAvatar(user.getUrlAvatar())
                .phone(user.getPhone())
                .status(user.getStatus())
                .userRoles(user.getRoles()
                        .stream()
                        .map(userHasRole -> userHasRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void updatePassword(Long userId, UpdatePassword request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("New password and confirm password are not the same");
            throw new ResourceNotFoundException("New password and confirm password are not the same");
        }
        User user = getUserById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.error("Old password is incorrect");
            throw new ResourceNotFoundException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("User password updated successfully");
    }

    @Override
    public void confirmUser(Long userId, String secretCode) {
        User user = getUserById(userId);

        log.info("User confirmed successfully service");
    }

    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy) {
        if (pageNo > 0) {
            pageNo = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase(("asc"))) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailReponse> reponses = users.stream()
                .map(user -> UserDetailReponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .address(user.getAddress())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .urlAvatar(user.getUrlAvatar())
                        .phone(user.getPhone())
                        .status(user.getStatus())
                        .userRoles(user.getRoles()
                                .stream()
                                .map(userHasRole -> userHasRole.getRole().getName())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
        log.info("Get all users successfully");
        return PageResponse.builder()
                .pageNo(pageNo + 1)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .totalElement(users.getTotalElements())
                .items(reponses)
                .build();
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
