package com.group3.apiserver.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "shopping_cart_item", schema = "isi", catalog = "")
@IdClass(ShoppingCartItemEntityPK.class)
public class ShoppingCartItemEntity {
    private int userId;
    private int productId;
    private int quantity;

    @Id
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        ShoppingCartItemEntity that = (ShoppingCartItemEntity) o;
        return userId == that.userId &&
                productId == that.productId &&
                quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId, quantity);
    }
}
