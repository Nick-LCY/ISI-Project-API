package com.group3.apiserver.dto.receiver.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateProductDTO {
    private Integer userId;
    private String token;
    private String name;
    private String category;
    private Double price;
}
