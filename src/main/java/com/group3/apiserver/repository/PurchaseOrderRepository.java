package com.group3.apiserver.repository;

import com.group3.apiserver.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Integer> {
}
