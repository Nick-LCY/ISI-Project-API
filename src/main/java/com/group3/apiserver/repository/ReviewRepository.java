package com.group3.apiserver.repository;

import com.group3.apiserver.entity.ReviewEntity;
import com.group3.apiserver.entity.ReviewEntityPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, ReviewEntityPK> {
    Page<ReviewEntity> findAllByProductId(Integer productId, Pageable pageable);
}
