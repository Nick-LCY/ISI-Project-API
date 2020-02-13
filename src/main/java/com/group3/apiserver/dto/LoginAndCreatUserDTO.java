package com.group3.apiserver.dto;

import lombok.Data;

@Data
public class LoginAndCreatUserDTO {
    private boolean success;
    private String token;
    private String message;
}
