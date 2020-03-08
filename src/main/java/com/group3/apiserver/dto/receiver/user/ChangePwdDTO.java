package com.group3.apiserver.dto.receiver.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChangePwdDTO {
    private Integer id;
    private String currentPwd;
    private String newPwd;
}
