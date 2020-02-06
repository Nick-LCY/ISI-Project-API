package com.group3.apiserver.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product", schema = "isi", catalog = "")
public class ProductEntity {
    private int id;
    private String name;
    private String category;
    private BigDecimal price;
    private byte outOfStock;
    private String thumbnailLocation;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Basic
    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Basic
    @Column(name = "out_of_stock")
    public byte getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(byte outOfStock) {
        this.outOfStock = outOfStock;
    }

    @Basic
    @Column(name = "thumbnail_location")
    public String getThumbnailLocation() {
        return thumbnailLocation;
    }

    public void setThumbnailLocation(String thumbnailLocation) {
        this.thumbnailLocation = thumbnailLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return id == that.id &&
                outOfStock == that.outOfStock &&
                Objects.equals(name, that.name) &&
                Objects.equals(category, that.category) &&
                Objects.equals(price, that.price) &&
                Objects.equals(thumbnailLocation, that.thumbnailLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, outOfStock, thumbnailLocation);
    }
}
