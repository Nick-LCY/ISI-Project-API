package com.group3.apiserver.dto.purchase.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PurchaseItemsDTO {
    private Integer poInfo;
    private Date purchaseDate;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Date shipmentDate;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Date cancelDate;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Integer cancelledBy;
    private Integer status;
    private List<PurchaseDetailDTO> purchaseItems;
}
