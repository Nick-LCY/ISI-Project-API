package com.group3.apiserver.repository;

import com.group3.apiserver.entity.ReviewEntity;
import com.group3.apiserver.entity.ReviewEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, ReviewEntityPK> {
    List<ReviewEntity> findAllByProductId(Integer productId);
}
