package com.group3.apiserver.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ReviewEntityPK implements Serializable {
    private int productId;
    private int purchaseOrderId;

    @Column(name = "product_id")
    @Id
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "purchase_order_id")
    @Id
    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntityPK that = (ReviewEntityPK) o;
        return productId == that.productId &&
                purchaseOrderId == that.purchaseOrderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, purchaseOrderId);
    }
}
