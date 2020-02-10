package com.group3.apiserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.entity.ProductDescriptionEntity;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.ProductPhotographEntity;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductDetailDTO extends ProductEntity {
    public ProductDetailDTO (ProductEntity productEntity) {
        this.setId(productEntity.getId());
        this.setName(productEntity.getName());
        this.setCategory(productEntity.getCategory());
        this.setPrice(productEntity.getPrice());
        this.setOutOfStock(productEntity.getOutOfStock());
        this.setThumbnailLocation(productEntity.getThumbnailLocation());
    }
    private Set<ProductPhotographEntity> productPhotographs;
    private Set<ProductDescriptionEntity> productDescriptions;
}
