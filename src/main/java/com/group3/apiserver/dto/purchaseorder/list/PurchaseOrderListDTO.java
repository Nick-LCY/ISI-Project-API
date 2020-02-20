package com.group3.apiserver.dto.purchaseorder.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseOrderListDTO {
    @JsonProperty("po_no")
    private Integer purchaseOrderId;
    private String purchaseDate;
    @JsonProperty("status")
    private String statusInString;
    private BigDecimal totalAmount;
}
