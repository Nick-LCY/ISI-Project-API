package com.group3.apiserver.dto.receiver.purchaseorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ModifyPurchaseOrderDTO {
    private Integer userId;
    private String token;
    @JsonProperty("po_no")
    private Integer purchaseOrderId;
    private String status;
}
