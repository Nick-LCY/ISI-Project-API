package com.group3.apiserver.dto.purchaseorder.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseOrderDTO {
    private Integer poNo;
    private String purchaseDate;
    private String shipmentDate;
    private String cancelDate;
    private Integer cancelledBy;
    private String status;
    private BigDecimal totalAmount;
    private List<PurchaseDetailDTO> purchaseItems;
}
