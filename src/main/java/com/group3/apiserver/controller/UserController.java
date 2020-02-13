package com.group3.apiserver.controller;

import com.group3.apiserver.dto.LoginAndCreatUserDTO;
import com.group3.apiserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public LoginAndCreatUserDTO createUser(@RequestParam(name = "email") String email,
                                           @RequestParam(name = "pwd") String pwd,
                                           @RequestParam(name = "name") String name,
                                           @RequestParam(name = "shipping_addr") String shippingAddr){
        return userService.creatUser(email, pwd, name, shippingAddr);
    }

    @PostMapping("/login")
    public LoginAndCreatUserDTO login(@RequestParam(name = "email") String email,
                                      @RequestParam(name = "pwd") String pwd) {
        return userService.login(email, pwd);
    }
}
