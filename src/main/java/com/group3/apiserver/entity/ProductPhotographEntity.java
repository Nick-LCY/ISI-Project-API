package com.group3.apiserver.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Entity
@Table(name = "product_photograph", schema = "isi")
public class ProductPhotographEntity {
    private int id;
    private String fileLocation;
    private int sequence;
    private int productId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "file_location")
    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Basic
    @Column(name = "sequence")
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Basic
    @Column(name = "product_id")
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
        ProductPhotographEntity that = (ProductPhotographEntity) o;
        return id == that.id &&
                sequence == that.sequence &&
                productId == that.productId &&
                Objects.equals(fileLocation, that.fileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileLocation, sequence, productId);
    }
}
