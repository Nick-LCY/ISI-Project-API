package com.group3.apiserver.dto.receiver.purchaseorder;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreatePurchaseOrderDTO {
    private Integer userId;
    private String token;
    private List<PurchaseItem> purchaseItems;
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class PurchaseItem {
        private Integer id;
        private Integer quantity;
    }
}
