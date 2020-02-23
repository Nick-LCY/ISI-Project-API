package com.group3.apiserver.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "review", schema = "isi")
@IdClass(ReviewEntityPK.class)
public class ReviewEntity {
    private int productId;
    private int purchaseOrderId;
    private int stars;
    private String content;
    private String commentDate;

    @Id
    @Column(name = "product_id")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Id
    @Column(name = "purchase_order_id")
    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Basic
    @Column(name = "stars")
    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Basic
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String review) {
        this.content = review;
    }

    @Basic
    @Column(name = "comment_date")
    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity that = (ReviewEntity) o;
        return productId == that.productId &&
                purchaseOrderId == that.purchaseOrderId &&
                stars == that.stars &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, purchaseOrderId, stars, content);
    }
}
