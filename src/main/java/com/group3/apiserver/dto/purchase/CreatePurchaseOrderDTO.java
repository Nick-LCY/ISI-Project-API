package com.group3.apiserver.dto.purchase;

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
        private Integer productId;
        private Integer quantity;
        public PurchaseItem(Integer productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
