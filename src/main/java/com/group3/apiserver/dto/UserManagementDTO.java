package com.group3.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UserManagementDTO {
    private boolean success;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer id;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String token;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String message;
}
