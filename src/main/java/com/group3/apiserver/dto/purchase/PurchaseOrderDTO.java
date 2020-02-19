package com.group3.apiserver.dto.purchase;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseOrderDTO {
    private Integer PoNo;
    private String purchaseDate;
    private String status;
    private BigDecimal totalAmount;
}
