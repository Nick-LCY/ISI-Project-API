package com.group3.apiserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductListItemDTO {
    private int id;
    private String name;
    private String category;
    private BigDecimal price;
    private boolean outOfStock;
    private String thumbnailLocation;
}