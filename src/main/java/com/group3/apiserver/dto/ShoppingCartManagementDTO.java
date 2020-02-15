package com.group3.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.entity.ProductEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShoppingCartManagementDTO {
    private boolean success;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<ShoppingCartItemDTO> shoppingCartItems;

    public void addShoppingCartItemDTO(ProductEntity productEntity, Integer quantity) {
        if (shoppingCartItems == null) {
            shoppingCartItems = new LinkedList<>();
        }
        shoppingCartItems.add(new ShoppingCartItemDTO(productEntity, quantity));
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ShoppingCartItemDTO {
        public ShoppingCartItemDTO(ProductEntity productEntity, Integer quantity) {
            this.product = productEntity;
            this.quantity = quantity;
        }
        private ProductEntity product;
        private Integer quantity;
    }
}
