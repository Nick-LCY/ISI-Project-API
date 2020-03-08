package com.group3.apiserver.controller;

import com.group3.apiserver.dto.*;
import com.group3.apiserver.dto.receiver.user.*;
import com.group3.apiserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:8080")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public UserManagementDTO createUser(@RequestBody CreateUserDTO createUserParas){
        return userService.creatUser(
                createUserParas.getEmail(),
                createUserParas.getPwd(),
                createUserParas.getName(),
                createUserParas.getShippingAddr()
        );
    }

    @PostMapping("/login")
    public UserManagementDTO login(@RequestBody LoginDTO loginParas) {
        return userService.login(loginParas.getEmail(), loginParas.getPwd());
    }

    @PostMapping(path = "/logout")
    public UserManagementDTO logout(@RequestBody LogoutDTO logoutParas) {
        return userService.logout(logoutParas.getId());
    }

    @PostMapping("/change_pwd")
    public UserManagementDTO changePwd(@RequestBody ChangePwdDTO changePwdParas) {
        return userService.changePwd(
                changePwdParas.getId(),
                changePwdParas.getCurrentPwd(),
                changePwdParas.getNewPwd()
        );
    }

    @PostMapping("/shopping_cart")
    public ShoppingCartManagementDTO modifyShoppingCart(@RequestBody ModifyShoppingCartDTO modifyShoppingCartParas) {
        return userService.modifyShoppingCartItem(
                modifyShoppingCartParas.getUserId(),
                modifyShoppingCartParas.getProductId(),
                modifyShoppingCartParas.getQuantity(),
                modifyShoppingCartParas.getToken());
    }

    @GetMapping("/shopping_cart")
    public ShoppingCartManagementDTO getShoppingCartItems(@RequestParam(name = "user_id") Integer userId,
                                                          @RequestParam(name = "token") String token) {
        return userService.getShoppingCartItems(userId, token);
    }

    @DeleteMapping("/shopping_cart")
    public ShoppingCartManagementDTO deleteShoppingCartItem(@RequestParam(name = "user_id") Integer userId,
                                                            @RequestParam(name = "product_id") Integer productId,
                                                            @RequestParam(name = "token") String token) {
        return userService.deleteShoppingCartItem(userId, productId, token);
    }

    @PostMapping("/review")
    public ReviewManagementDTO saveUserReview(@RequestBody SaveUserReviewDTO saveUserReviewParas) {
        return userService.saveUserReview(
                saveUserReviewParas.getPurchaseOrderId(),
                saveUserReviewParas.getProductId(),
                saveUserReviewParas.getToken(),
                saveUserReviewParas.getStars(),
                saveUserReviewParas.getContent());
    }

    @GetMapping("/reviews")
    public PaginationDTO<ReviewDTO> getReviews(@RequestParam(name = "product_id") Integer productId,
                                               @RequestParam(name = "page") Integer page) {
        return userService.getReviews(productId, page);
    }
}
