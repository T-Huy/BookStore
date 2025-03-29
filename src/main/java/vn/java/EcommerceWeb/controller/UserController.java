package vn.java.EcommerceWeb.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.UpdatePassword;
import vn.java.EcommerceWeb.dto.request.UserRequest;
import vn.java.EcommerceWeb.dto.response.ResponseData;
import vn.java.EcommerceWeb.dto.response.ResponseError;
import vn.java.EcommerceWeb.service.CloudinaryService;
import vn.java.EcommerceWeb.service.UserService;

@RestController
@RequestMapping("/v1/api/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Admin create user")
    @PostMapping("/")
    public ResponseData<?> createUser(
            @RequestPart @Valid UserRequest request, @RequestPart(value = "file",
                                                                  required = true) MultipartFile file) {
        try {
            Long userId = userService.createUser(request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Create user successfully", userId);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create user failed");
        }
    }

    @Operation(summary = "Admin update user")
    @PutMapping("/{id}")
    public ResponseData<?> updateUser(
            @PathVariable @Min(1) Long id,
            @RequestPart @Valid UserRequest request, @RequestPart(value = "file",
                                                                  required = true) MultipartFile file) {
        try {
            userService.updateUser(id, request, file);
            return new ResponseData<>(HttpStatus.OK.value(), "Updated user successfully");
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Updated user failed");
        }
    }

    @Operation(summary = "Admin delete user")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable @Min(1) Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Deleted user successfully");
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Deleted user failed");
        }
    }

    @Operation(summary = "Admin get all users")
    @GetMapping("/")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0",
                                                     required = false) int pageNo, @RequestParam(defaultValue = "20",
                                                                                                 required = false) int pageSize,
                                       @RequestParam(defaultValue = "updatedAt:ASC",
                                                     required = false) String sortBy) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get all users successfully", userService.getAllUsers(pageNo, pageSize, sortBy));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all users failed");
        }
    }

    @Operation(summary = "Admin get user by id")
    @GetMapping("/{id}")
    public ResponseData<?> getDetailUserById(@PathVariable @Min(1) Long id) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get user by id successfully", userService.getDetailUserById(id));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get user by id failed");
        }
    }

    @Operation(summary = "User change user password")
    @PatchMapping("/{id}/password")
    public ResponseData<?> updatePassword(@PathVariable @Min(1) Long id, @RequestBody @Valid UpdatePassword request) {
        try {
            userService.updatePassword(id, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Change password successfully");
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change password failed");
        }
    }
}
