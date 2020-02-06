package com.group3.apiserver.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class PurchaseDetailEntityPK implements Serializable {
    private int purchaseOrderId;
    private int productId;

    @Column(name = "purchase_order_id")
    @Id
    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Column(name = "product_id")
    @Id
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseDetailEntityPK that = (PurchaseDetailEntityPK) o;
        return purchaseOrderId == that.purchaseOrderId &&
                productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderId, productId);
    }
}
