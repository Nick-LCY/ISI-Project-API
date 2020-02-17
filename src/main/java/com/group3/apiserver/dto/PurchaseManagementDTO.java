package com.group3.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseManagementDTO {
    private boolean success;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String message;
    // fixme: Still show even PoInfo is null
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<PurchaseOrderDTO> PoInfo;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PurchaseDetailDTO purchaseDetail;
}
