package com.group3.apiserver.dto.sender;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.group3.apiserver.dto.receiver.product.AddDescriptionDTO;
import com.group3.apiserver.entity.ProductDescriptionEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductManagementDTO {
    private Boolean success;
    private String message;
    private Integer productId;
    private ProductDetailDTO productDetail;
    private List<DescriptionDTO> productDescriptions;

    public void addProductDetailDTO(Integer id) {
        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        productDetailDTO.setId(id);
        this.productDetail = productDetailDTO;
    }

    public void addProductDetailDTO(String name, String category, Double price) {
        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        productDetailDTO.setName(name);
        productDetailDTO.setCategory(category);
        productDetailDTO.setPrice(price);
        productDetail = productDetailDTO;
    }

    public void addProductDescriptions(List<ProductDescriptionEntity> productDescriptionList) {
        productDescriptions = new LinkedList<>();
        for (ProductDescriptionEntity productDescription :
                productDescriptionList) {
            DescriptionDTO descriptionDTO = new DescriptionDTO();
            descriptionDTO.setId(productDescription.getId());
            descriptionDTO.setAttributeName(productDescription.getAttributeName());
            descriptionDTO.setAttributeValue(productDescription.getAttributeValue());
            descriptionDTO.setProductId(productDescription.getProductId());
            descriptionDTO.setSequence(productDescription.getSequence());
            productDescriptions.add(descriptionDTO);
        }

    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static class ProductDetailDTO {
        private Integer id;
        private String name;
        private String category;
        private Double price;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static class DescriptionDTO {
        private Integer id;
        private String attributeName;
        private String attributeValue;
        private Integer sequence;
        private Integer productId;
    }
}
