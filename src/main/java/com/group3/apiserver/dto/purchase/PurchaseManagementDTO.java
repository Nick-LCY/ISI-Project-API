package com.group3.apiserver.dto.purchase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseItemsDTO;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseManagementDTO {
    private boolean success;
    private String message;
    private PaginationDTO<PurchaseOrderDTO> PoInfo;
    private PurchaseItemsDTO purchaseDetail;
}
