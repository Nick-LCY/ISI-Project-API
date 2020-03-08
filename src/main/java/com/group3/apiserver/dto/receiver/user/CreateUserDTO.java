package com.group3.apiserver.dto.receiver.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateUserDTO {
    private String email;
    private String pwd;
    private String name;
    private String shippingAddr;
}
