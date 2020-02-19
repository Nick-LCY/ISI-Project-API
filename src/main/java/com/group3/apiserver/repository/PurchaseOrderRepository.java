package com.group3.apiserver.repository;

import com.group3.apiserver.entity.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Integer> {
    @Query("select po from PurchaseOrderEntity po where po.userId = ?1 and (po.status = ?2 or po.status = ?3)")
    Page<PurchaseOrderEntity> findAllByUserIdAndTwoStatus(int userId, int status1, int status2, Pageable pageable);
//    @Query("select PurchaseOrderEntity from PurchaseOrderEntity where userId = ?1 and (status = ?2 or status = ?3)")
//    Page<PurchaseOrderEntity> findAllByUserIdAndTwoStatus(int userId, int status1, int status2);
}
