package com.group3.apiserver.repository;

import com.group3.apiserver.entity.PurchaseDetailEntity;
import com.group3.apiserver.entity.PurchaseDetailEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetailEntity, PurchaseDetailEntityPK> {
}
