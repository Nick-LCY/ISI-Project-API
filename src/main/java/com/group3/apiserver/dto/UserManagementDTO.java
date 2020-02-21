package com.group3.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserManagementDTO {
    private boolean success;
    private Integer id;
    private String name;
    private String token;
    private String message;
}
