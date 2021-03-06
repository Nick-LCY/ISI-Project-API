package com.group3.apiserver.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "purchase_order", schema = "isi")
public class PurchaseOrderEntity {
    private int id;
    private String purchaseDate;
    private String shipmentDate;
    private String cancelDate;
    private Integer cancelledBy;
    private int status;
    private int userId;
    private BigDecimal totalAmount;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "purchase_date")
    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Basic
    @Column(name = "shipment_date")
    public String getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    @Basic
    @Column(name = "cancel_date")
    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    @Basic
    @Column(name = "cancelled_by")
    public Integer getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Integer cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    @Basic
    @Column(name = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Basic
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderEntity that = (PurchaseOrderEntity) o;
        return id == that.id &&
                status == that.status &&
                userId == that.userId &&
                Objects.equals(purchaseDate, that.purchaseDate) &&
                Objects.equals(shipmentDate, that.shipmentDate) &&
                Objects.equals(cancelDate, that.cancelDate) &&
                Objects.equals(cancelledBy, that.cancelledBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, purchaseDate, shipmentDate, cancelDate, cancelledBy, status, userId);
    }

    @Basic
    @Column(name = "total_amount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}