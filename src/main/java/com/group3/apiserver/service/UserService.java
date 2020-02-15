package com.group3.apiserver.service;

import com.group3.apiserver.dto.ShoppingCartManagementDTO;
import com.group3.apiserver.dto.UserManagementDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.ShoppingCartItemEntity;
import com.group3.apiserver.entity.ShoppingCartItemEntityPK;
import com.group3.apiserver.entity.UserEntity;
import com.group3.apiserver.repository.ProductRepository;
import com.group3.apiserver.repository.ShoppingCartItemRepository;
import com.group3.apiserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final String AUTHENTICATION_FAIL = "Authentication failed.";
    private final String PRODUCT_NOT_FOUND = "Product not found.";
    private final String USER_NOT_FOUND = "User not found.";

    private UserRepository userRepository;
    private ShoppingCartItemRepository shoppingCartItemRepository;
    private ProductRepository productRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setShoppingCartItemRepository(ShoppingCartItemRepository shoppingCartItemRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

    public UserManagementDTO logout(Integer id) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's user id
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setToken(null);
            user.setCreateTime(null);
            userRepository.save(user);
            userManagementDTO.setSuccess(true);
        } else {
            userManagementDTO.setSuccess(false);
            userManagementDTO.setMessage(USER_NOT_FOUND);
        }
        return userManagementDTO;
    }

    public UserManagementDTO changePwd(Integer id, String oldPwd, String newPwd) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's user id
        Optional<UserEntity> userOptional = userRepository.findById(id);
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
            userManagementDTO.setMessage(USER_NOT_FOUND);
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
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        // Authenticate user
        if (userAuthentication(userId, token)) {
            ShoppingCartItemEntity shoppingCartItem = new ShoppingCartItemEntity();
            shoppingCartItem.setUserId(userId);
            // Check if there is really has a product with this id
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                // Save changes into database
                shoppingCartItem.setProductId(productId);
                shoppingCartItem.setQuantity(quantity);
                shoppingCartItemRepository.save(shoppingCartItem);
                // Get updated shopping cart items from database
                for (ShoppingCartItemEntity e :
                        shoppingCartItemRepository.findAllByUserId(userId)) {
                    // The newest inserted one would be null if don't use this way
                    if (e.getProductId() == productOptional.get().getId()) {
                        shoppingCartManagementDTO.addShoppingCartItemDTO(productOptional.get(), e.getQuantity());
                    } else {
                        shoppingCartManagementDTO.addShoppingCartItemDTO(e.getProduct(), e.getQuantity());
                    }
                }
                shoppingCartManagementDTO.setSuccess(true);
                return shoppingCartManagementDTO;
            } else {
                shoppingCartManagementDTO.setSuccess(false);
                shoppingCartManagementDTO.setMessage(PRODUCT_NOT_FOUND);
            }
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }

    public ShoppingCartManagementDTO getShoppingCartItems(Integer userId, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        if (userAuthentication(userId, token)) {
            for (ShoppingCartItemEntity e :
                    shoppingCartItemRepository.findAllByUserId(userId)) {
                shoppingCartManagementDTO.addShoppingCartItemDTO(e.getProduct(), e.getQuantity());
            }
            shoppingCartManagementDTO.setSuccess(true);
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }

    public ShoppingCartManagementDTO deleteShoppingCartItem(Integer userId, Integer productId, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        if (userAuthentication(userId, token)) {
            ShoppingCartItemEntityPK shoppingCartItemPK = new ShoppingCartItemEntityPK();
            shoppingCartItemPK.setProductId(productId);
            shoppingCartItemPK.setUserId(userId);
            if (shoppingCartItemRepository.findById(shoppingCartItemPK).isPresent()) {
                shoppingCartItemRepository.deleteById(shoppingCartItemPK);
                shoppingCartManagementDTO.setSuccess(true);
            } else {
                shoppingCartManagementDTO.setSuccess(false);
                shoppingCartManagementDTO.setMessage(PRODUCT_NOT_FOUND);
            }
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }
}