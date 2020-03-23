package com.group3.apiserver.dto.receiver.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.entity.ProductDescriptionEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ManageDescriptionDTO {
    private List<DescriptionDTO> productDescriptions;
    private Integer userId;
    private String token;
    private Integer productId;

    public List<ProductDescriptionEntity> getProductDescriptionsInEntity() {
        List<ProductDescriptionEntity> productDescriptionList = new LinkedList<>();
        for (DescriptionDTO descriptionDTO :
                productDescriptions) {
            ProductDescriptionEntity productDescription = new ProductDescriptionEntity();
            if (descriptionDTO.getId() != null) {
                productDescription.setId(descriptionDTO.getId());
            }
            productDescription.setAttributeName(descriptionDTO.getAttributeName());
            productDescription.setAttributeValue(descriptionDTO.getAttributeValue());
            if (productId != null) {
                productDescription.setProductId(productId);
            }
            productDescriptionList.add(productDescription);
        }
        return productDescriptionList;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class DescriptionDTO {
        private String attributeName;
        private String attributeValue;
        private Integer id;
    }
}
