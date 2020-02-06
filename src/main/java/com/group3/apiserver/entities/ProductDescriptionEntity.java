package com.group3.apiserver.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "product_description", schema = "isi", catalog = "")
public class ProductDescriptionEntity {
    private int id;
    private String content;
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
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        ProductDescriptionEntity that = (ProductDescriptionEntity) o;
        return id == that.id &&
                sequence == that.sequence &&
                productId == that.productId &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, sequence, productId);
    }
}
