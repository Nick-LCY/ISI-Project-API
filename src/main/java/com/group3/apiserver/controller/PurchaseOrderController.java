package com.group3.apiserver.controller;

import com.group3.apiserver.dto.receiver.CreatePurchaseOrderDTO;
import com.group3.apiserver.dto.purchaseorder.PurchaseManagementDTO;
import com.group3.apiserver.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:8083")
public class PurchaseOrderController {
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping("/purchase_order")
    public PurchaseManagementDTO createPurchaseOrder(@RequestBody CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        return purchaseOrderService.createPurchaseOrder(createPurchaseOrderDTO);
    }

    @PatchMapping("/purchase_order")
    public PurchaseManagementDTO updatePurchaseOrderStatus(@RequestParam(name = "user_id") Integer userId,
                                                           @RequestParam(name = "token") String token,
                                                           @RequestParam(name = "po_no") Integer purchaseOrderId,
                                                           @RequestParam(name = "status") Integer status) {
        return purchaseOrderService.updatePurchaseOrderStatus(userId, token, purchaseOrderId, status);
    }

    @GetMapping("/purchase_orders")
    public PurchaseManagementDTO getPurchaseOrders(@RequestParam(name = "user_id") Integer userId,
                                                   @RequestParam(name = "token") String token) {
        return purchaseOrderService.getPurchaseOrders(userId, token);
    }

    @GetMapping("/vendor_purchase_orders")
    public PurchaseManagementDTO getVendorPurchaseOrders(@RequestParam(name = "user_id") Integer userId,
                                                         @RequestParam(name = "token") String token) {
        return purchaseOrderService.getPurchaseOrders(userId, token);
    }

    @GetMapping("/purchase_order")
    public PurchaseManagementDTO getPurchaseOrder(@RequestParam(name = "user_id") Integer userId,
                                                  @RequestParam(name = "token") String token,
                                                  @RequestParam(name = "po_no") Integer purchaseOrderId) {
        return purchaseOrderService.getPurchaseOrder(userId, token, purchaseOrderId);
    }
}
