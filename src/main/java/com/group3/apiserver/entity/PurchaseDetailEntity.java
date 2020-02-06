package com.group3.apiserver.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "purchase_detail", schema = "isi", catalog = "")
@IdClass(PurchaseDetailEntityPK.class)
public class PurchaseDetailEntity {
    private int purchaseOrderId;
    private int productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;

    @Id
    @Column(name = "purchase_order_id")
    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Id
    @Column(name = "product_id")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Basic
    @Column(name = "product_name")
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Basic
    @Column(name = "product_price")
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    @Basic
    @Column(name = "quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseDetailEntity that = (PurchaseDetailEntity) o;
        return purchaseOrderId == that.purchaseOrderId &&
                productId == that.productId &&
                quantity == that.quantity &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(productPrice, that.productPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderId, productId, productName, productPrice, quantity);
    }
}
