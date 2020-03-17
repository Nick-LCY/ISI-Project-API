package com.group3.apiserver.dto.sender;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductManagementDTO {
    private Boolean success;
    private String message;
    private Integer productId;
    private ProductDetailDTO productDetail;

    public void addProductDetailDTO(Integer id) {
        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        productDetailDTO.setId(id);
        this.productDetail = productDetailDTO;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private class ProductDetailDTO {
        private Integer id;
    }
}
