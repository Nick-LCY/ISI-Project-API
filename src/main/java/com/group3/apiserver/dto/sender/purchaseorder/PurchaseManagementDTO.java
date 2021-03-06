package com.group3.apiserver.dto.sender.purchaseorder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.dto.sender.purchaseorder.detail.PurchaseOrderDTO;
import com.group3.apiserver.dto.sender.purchaseorder.list.PurchaseOrderListDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseManagementDTO {
    private boolean success;
    private String message;
    @JsonProperty("po_info")
    private List<PurchaseOrderListDTO> purchaseOrderListDTOList;
    private PurchaseOrderDTO purchaseDetail;
}
