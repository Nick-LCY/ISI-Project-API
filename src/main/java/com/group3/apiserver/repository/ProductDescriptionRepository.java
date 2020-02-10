package com.group3.apiserver.repository;

import com.group3.apiserver.entity.ProductDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescriptionEntity, Integer> {
    Set<ProductDescriptionEntity> findAllByProductId(Integer productId);
}
