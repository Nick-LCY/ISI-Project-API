package com.group3.apiserver.dto.sender.purchaseorder.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PurchaseOrderListDTO {
    @JsonProperty("po_no")
    private Integer purchaseOrderId;
    private String purchaseDate;
    @JsonProperty("status")
    private String statusInString;
    private BigDecimal totalAmount;
    private String customerName;
}
