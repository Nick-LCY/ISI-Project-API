package com.group3.apiserver.dto.purchase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseItemsDTO;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseManagementDTO {
    private boolean success;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String message;
    // fixme: Still show even PoInfo is null
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PaginationDTO<PurchaseOrderDTO> PoInfo;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PurchaseItemsDTO purchaseDetail;
}
