package com.group3.apiserver.service;

import com.group3.apiserver.dto.ShoppingCartManagementDTO;
import com.group3.apiserver.dto.UserManagementDTO;
import com.group3.apiserver.entity.ShoppingCartItemEntity;
import com.group3.apiserver.entity.UserEntity;
import com.group3.apiserver.repository.ProductRepository;
import com.group3.apiserver.repository.ShoppingCartItemRepository;
import com.group3.apiserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final String AUTHENTICATION_FAIL = "Authentication failed";
    private UserRepository userRepository;
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setShoppingCartItemRepository(ShoppingCartItemRepository shoppingCartItemRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
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

    // TODO: use user id to logout
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

    // TODO: user user id to change password
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

    private Boolean userAuthentication(Integer id, String token) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        return userOptional.filter(userEntity -> Objects.equals(userEntity.getToken(), token)).isPresent();
    }

    public ShoppingCartManagementDTO modifyShoppingCartItem(Integer userId, Integer productId, Integer quantity, String token) {
        // Authenticate user
        if (userAuthentication(userId, token)) {
            // fixme: return null for the newest inset object
            ShoppingCartItemEntity shoppingCartItem = new ShoppingCartItemEntity();
            shoppingCartItem.setUserId(userId);
            shoppingCartItem.setProductId(productId);
            shoppingCartItem.setQuantity(quantity);
            shoppingCartItemRepository.saveAndFlush(shoppingCartItem);
            return getShoppingCartItems(userId, token);
        } else {
            ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(AUTHENTICATION_FAIL);
            return shoppingCartManagementDTO;
        }
    }

    public ShoppingCartManagementDTO getShoppingCartItems(Integer userId, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        if (userAuthentication(userId, token)) {
            for (ShoppingCartItemEntity e :
                    shoppingCartItemRepository.findAll()) {
                shoppingCartManagementDTO.addShoppingCartItemDTO(e.getProduct(), e.getQuantity());
            }
            shoppingCartManagementDTO.setSuccess(true);
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }
}