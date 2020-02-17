package com.group3.apiserver.service;

import com.group3.apiserver.dto.purchase.CreatePurchaseOrderDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseDetailDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseItemsDTO;
import com.group3.apiserver.dto.purchase.PurchaseManagementDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.PurchaseDetailEntity;
import com.group3.apiserver.entity.PurchaseOrderEntity;
import com.group3.apiserver.message.ErrorMessage;
import com.group3.apiserver.repository.ProductRepository;
import com.group3.apiserver.repository.PurchaseDetailRepository;
import com.group3.apiserver.repository.PurchaseOrderRepository;
import com.group3.apiserver.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;

@Service
public class PurchaseOrderService {
    private AuthenticationUtil authenticationUtil;
    private PurchaseDetailRepository purchaseDetailRepository;
    private ProductRepository productRepository;
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public void setAuthenticationUtil(AuthenticationUtil authenticationUtil) {
        this.authenticationUtil = authenticationUtil;
    }

    @Autowired
    public void setPurchaseDetailRepository(PurchaseDetailRepository purchaseDetailRepository) {
        this.purchaseDetailRepository = purchaseDetailRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setPurchaseOrderRepository(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public PurchaseManagementDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        if (authenticationUtil.userAuthentication(createPurchaseOrderDTO.getUserId(), createPurchaseOrderDTO.getToken())) {
            PurchaseItemsDTO purchaseItemsDTO = new PurchaseItemsDTO();
            PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity();
            // Generate a unique PoNo
            Random random = new Random();
            int purchaseOrderId = random.nextInt(90000000) + 10000000;
            while (purchaseOrderRepository.findById(purchaseOrderId).isPresent()) {
                purchaseOrderId = random.nextInt(90000000) + 10000000;
            }
            // Purchase order's basic info
            purchaseOrderEntity.setId(purchaseOrderId);
            purchaseItemsDTO.setId(purchaseOrderId);

            purchaseOrderEntity.setUserId(createPurchaseOrderDTO.getUserId());
            // fixme: Wrong time zone now
            Date date = new Date();
            purchaseOrderEntity.setPurchaseDate(date);
            purchaseItemsDTO.setPurchaseDate(date);

            purchaseOrderEntity.setStatus(0);
            purchaseItemsDTO.setStatus(0);
            // Save purchase order
            purchaseOrderRepository.save(purchaseOrderEntity);
            // Save purchase details
            purchaseItemsDTO.setPurchaseItems(new LinkedList<>());
            for (CreatePurchaseOrderDTO.PurchaseItem item:
                    createPurchaseOrderDTO.getPurchaseItems()) {
                PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                PurchaseDetailDTO purchaseDetailDTO = new PurchaseDetailDTO();
                // Purchase detail's basic info
                purchaseDetail.setPurchaseOrderId(purchaseOrderId);

                purchaseDetail.setProductId(item.getProductId());
                purchaseDetailDTO.setProductId(item.getProductId());

                purchaseDetail.setQuantity(item.getQuantity());
                purchaseDetailDTO.setQuantity(item.getQuantity());
                // Historical data
                Optional<ProductEntity> productOptional = productRepository.findById(item.getProductId());
                if (productOptional.isPresent()) {
                    purchaseDetail.setProductName(productOptional.get().getName());
                    purchaseDetailDTO.setProductName(productOptional.get().getName());

                    purchaseDetail.setProductPrice(productOptional.get().getPrice());
                    purchaseDetailDTO.setProductPrice(productOptional.get().getPrice());
                } else {
                    purchaseManagementDTO.setSuccess(false);
                    purchaseManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
                    return purchaseManagementDTO;
                }
                // Save purchase detail
                purchaseItemsDTO.getPurchaseItems().add(purchaseDetailDTO);
                purchaseDetailRepository.save(purchaseDetail);
            }
            purchaseManagementDTO.setSuccess(true);
            purchaseManagementDTO.setPurchaseDetail(purchaseItemsDTO);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }
}
