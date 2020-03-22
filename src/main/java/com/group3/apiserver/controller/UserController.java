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
    public UserManagementDTO createUser(@RequestBody CreateUserDTO createUserParams){
        return userService.creatUser(
                createUserParams.getEmail(),
                createUserParams.getPwd(),
                createUserParams.getName(),
                createUserParams.getShippingAddr()
        );
    }

    @PostMapping("/login")
    public UserManagementDTO login(@RequestBody LoginDTO loginParams) {
        return userService.login(loginParams.getEmail(), loginParams.getPwd());
    }

    @PostMapping(path = "/logout")
    public UserManagementDTO logout(@RequestBody LogoutDTO logoutParams) {
        return userService.logout(logoutParams.getId());
    }

    @PostMapping("/change_pwd")
    public UserManagementDTO changePwd(@RequestBody ChangePwdDTO changePwdParams) {
        return userService.changePwd(
                changePwdParams.getId(),
                changePwdParams.getCurrentPwd(),
                changePwdParams.getNewPwd()
        );
    }

    @PostMapping("/shopping_cart")
    public ShoppingCartManagementDTO modifyShoppingCart(@RequestBody ModifyShoppingCartDTO modifyShoppingCartParams) {
        return userService.modifyShoppingCartItem(
                modifyShoppingCartParams.getUserId(),
                modifyShoppingCartParams.getProductId(),
                modifyShoppingCartParams.getQuantity(),
                modifyShoppingCartParams.getToken());
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
