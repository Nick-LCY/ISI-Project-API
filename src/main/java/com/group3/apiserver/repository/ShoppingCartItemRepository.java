package com.group3.apiserver.repository;

import com.group3.apiserver.entity.ShoppingCartItemEntity;
import com.group3.apiserver.entity.ShoppingCartItemEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItemEntity, ShoppingCartItemEntityPK> {
    List<ShoppingCartItemEntity> findAllByUserId(Integer userId);

    Optional<ShoppingCartItemEntity> findByProductId(int i);
}
