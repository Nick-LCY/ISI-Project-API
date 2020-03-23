package com.group3.apiserver.dto.sender;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.entity.ProductEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ShoppingCartManagementDTO {
    private boolean success;
    private String message;
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
        public ShoppingCartItemDTO(ProductEntity product, Integer quantity) {
            this.id = product.getId();
            this.name = product.getName();
            this.category = product.getCategory();
            this.price = product.getPrice();
            this.outOfStock = product.getOutOfStock();
            this.thumbnailLocation = product.getThumbnailLocation();
            this.quantity = quantity;
            this.subtotal = this.quantity * this.price.doubleValue();
        }

        private Integer id;
        private String name;
        private String category;
        @JsonProperty("single_price")
        private BigDecimal price;
        private Boolean outOfStock;
        private String thumbnailLocation;
        private Integer quantity;
        private Double subtotal;
    }
}
