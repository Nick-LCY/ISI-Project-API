package com.group3.apiserver.dto.receiver.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SaveUserReviewDTO {
    private Integer purchaseOrderId;
    private Integer productId;
    private String token;
    private Integer stars = 5;
    private String content;
}
