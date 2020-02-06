package com.group3.apiserver.entities;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "purchase_order", schema = "isi", catalog = "")
public class PurchaseOrderEntity {
    private int id;
    private Date purchaseDate;
    private Date shipmentDate;
    private Date cancelDate;
    private Integer cancelledBy;
    private int status;
    private int userId;

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
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Basic
    @Column(name = "shipment_date")
    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    @Basic
    @Column(name = "cancel_date")
    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
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
}
