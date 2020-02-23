package com.group3.apiserver.controller;

import com.group3.apiserver.dto.*;
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
    public UserManagementDTO createUser(@RequestParam(name = "email") String email,
                                        @RequestParam(name = "pwd") String pwd,
                                        @RequestParam(name = "name") String name,
                                        @RequestParam(name = "shipping_addr") String shippingAddr){
        return userService.creatUser(email, pwd, name, shippingAddr);
    }

    @PostMapping("/login")
    public UserManagementDTO login(@RequestParam(name = "email") String email,
                                   @RequestParam(name = "pwd") String pwd) {
        return userService.login(email, pwd);
    }

    @PostMapping("/logout")
    public UserManagementDTO logout(@RequestParam(name = "id") Integer id) {
        return userService.logout(id);
    }

    @PostMapping("/change_pwd")
    public UserManagementDTO changePwd(@RequestParam(name = "id") Integer id,
                                       @RequestParam(name = "old_pwd") String oldPwd,
                                       @RequestParam(name = "new_pwd") String newPwd) {
        return userService.changePwd(id, oldPwd, newPwd);
    }

    @PostMapping("/shopping_cart")
    public ShoppingCartManagementDTO modifyShoppingCart(@RequestParam(name = "user_id") Integer userId,
                                                        @RequestParam(name = "product_id") Integer productId,
                                                        @RequestParam(name = "quantity") Integer quantity,
                                                        @RequestParam(name = "token") String token) {
        return userService.modifyShoppingCartItem(userId, productId, quantity, token);
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
    public ReviewManagementDTO saveUserReview(@RequestParam(name = "po_no") Integer purchaseOrderId,
                                              @RequestParam(name = "product_id") Integer productId,
                                              @RequestParam(name = "token") String token,
                                              @RequestParam(name = "rating", defaultValue = "5") Integer rating,
                                              @RequestParam(name = "content") String content) {
        return userService.saveUserReview(purchaseOrderId, productId, token, rating, content);
    }

    @GetMapping("/reviews")
    public PaginationDTO<ReviewDTO> getReviews(@RequestParam(name = "product_id") Integer productId,
                                               @RequestParam(name = "page") Integer page) {
        return userService.getReviews(productId, page);
    }
}
