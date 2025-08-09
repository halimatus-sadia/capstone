package com.example.capstone.auth;

import com.example.capstone.common.MinIOService;
import com.example.capstone.utils.MultipartUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MinIOService minIOService;

    @Autowired
    protected UserRepository userRepository;

    public abstract UserResponse toResponse(User entity);

    @Mapping(target = "profileImageFilePath", expression = "java(getProfileImageFilePath(request.getImage()))")
    @Mapping(target = "password", expression = "java(getPassword(request, user.getPassword()))")
    @Mapping(target = "username", expression = "java(getUsername(request))")
    public abstract void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    protected String getProfileImageFilePath(MultipartFile image) {
        if (!MultipartUtils.isEmpty(image)) {
            return this.minIOService.uploadFile(image);
        }
        return null;
    }

    protected String getUsername(UserUpdateRequest request) {
        boolean exists = userRepository.existsByUsernameEqualsIgnoreCaseAndIdNot(request.getUsername(), request.getId());
        if (exists) {
            throw new RuntimeException("Username not available.");
        }
        return request.getUsername();
    }

    protected String getPassword(UserUpdateRequest request, String encOldPassword) {
        if (StringUtils.isNotEmpty(request.getNewPassword())) {
            if (StringUtils.equals(request.getNewPassword(), request.getConfirmPassword())) {
                return passwordEncoder.encode(request.getNewPassword());
            }
            throw new RuntimeException("Passwords do not match.");
        }
        return encOldPassword;
    }
}
