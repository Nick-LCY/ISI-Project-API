package com.group3.apiserver.dto.purchaseorder.detail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseDetailDTO {
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
}
