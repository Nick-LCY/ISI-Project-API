package com.group3.apiserver.repository;

import com.group3.apiserver.entity.ProductPhotographEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductPhotographRepository extends JpaRepository<ProductPhotographEntity, Integer> {
    Set<ProductPhotographEntity> findAllByProductId(Integer productId);
}
