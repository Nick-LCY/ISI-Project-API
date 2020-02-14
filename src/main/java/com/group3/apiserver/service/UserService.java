package com.group3.apiserver.service;

import com.group3.apiserver.dto.UserManagementDTO;
import com.group3.apiserver.entity.UserEntity;
import com.group3.apiserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserManagementDTO creatUser(String email, String pwd, String name, String shippingAddr) {
        UserManagementDTO creatUserDTO = new UserManagementDTO();
        // Check if email has been used.
        if (userRepository.findByEmail(email).isPresent()) {
            creatUserDTO.setSuccess(false);
            creatUserDTO.setMessage("E-Mail address has already been used.");
        } else {
            String validator = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08" +
                    "\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?" +
                    ":(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][" +
                    "0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x0" +
                    "1-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
            // Validate user's email address
            if (Pattern.matches(validator, email)) {
                UserEntity user = new UserEntity();
                user.setEmail(email);
                user.setPwd(pwd);
                user.setName(name);
                user.setShippingAddr(shippingAddr);
                creatUserDTO.setSuccess(true);
                // Generate token and create time
                creatUserDTO.setToken(setTokenAndCreateTime(user));
                userRepository.save(user);
                // Get auto generated id from database and set to DTO
                Optional<UserEntity> userOptional = userRepository.findByEmail(user.getEmail());
                userOptional.ifPresent(userEntity -> creatUserDTO.setId(userEntity.getId()));
            } else {
                creatUserDTO.setSuccess(false);
                creatUserDTO.setMessage("Invalid E-Mail address.");
            }
        }
        return creatUserDTO;
    }

    public UserManagementDTO login(String email, String pwd) {
        UserManagementDTO loginDTO = new UserManagementDTO();
        // Find user by it's email
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // Validate user's pwd
            if (Objects.equals(pwd, user.getPwd())) {
                loginDTO.setSuccess(true);
                // Generate token and create time
                loginDTO.setToken(setTokenAndCreateTime(user));
                user = userRepository.save(user);
                loginDTO.setId(user.getId());
            } else {
                loginDTO.setSuccess(false);
            }
        } else {
            loginDTO.setSuccess(false);
        }
        return loginDTO;
    }

    public UserManagementDTO logout(String email) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's email
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setToken(null);
            user.setCreateTime(null);
            userRepository.save(user);
            userManagementDTO.setSuccess(true);
        } else {
            userManagementDTO.setSuccess(false);
            userManagementDTO.setMessage("User not found.");
        }
        return userManagementDTO;
    }

    public UserManagementDTO changePwd(String email, String oldPwd, String newPwd) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's email
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // Validate user's password
            if (Objects.equals(oldPwd, user.getPwd())) {
                // Set new password
                user.setPwd(newPwd);
                userManagementDTO.setSuccess(true);
                userManagementDTO.setToken(setTokenAndCreateTime(user));
                user = userRepository.save(user);
                userManagementDTO.setId(user.getId());
            } else {
                userManagementDTO.setSuccess(false);
                userManagementDTO.setMessage("Wrong password.");
            }
        } else {
            userManagementDTO.setSuccess(false);
            userManagementDTO.setMessage("User not found.");
        }
        return userManagementDTO;
    }

    private String setTokenAndCreateTime(UserEntity user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setToken(token);
        user.setCreateTime(Long.toString(System.currentTimeMillis()));
        return token;
    }
}