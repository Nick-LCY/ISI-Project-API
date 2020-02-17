package com.group3.apiserver.service;

import com.group3.apiserver.dto.CreatePurchaseOrderDTO;
import com.group3.apiserver.dto.PurchaseDetailDTO;
import com.group3.apiserver.dto.PurchaseManagementDTO;
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
            PurchaseDetailDTO purchaseDetailDTO = new PurchaseDetailDTO();
            PurchaseOrderEntity purchaseOrderEntity = new PurchaseOrderEntity();
            // Generate a unique PoNo
            Random random = new Random();
            int purchaseOrderId = random.nextInt(90000000) + 10000000;
            while (purchaseOrderRepository.findById(purchaseOrderId).isPresent()) {
                purchaseOrderId = random.nextInt(90000000) + 10000000;
            }
            // Purchase order's basic info
            purchaseOrderEntity.setId(purchaseOrderId);
            purchaseDetailDTO.setId(purchaseOrderId);

            purchaseOrderEntity.setUserId(createPurchaseOrderDTO.getUserId());
            // fixme: Wrong time zone now
            Date date = new Date();
            purchaseOrderEntity.setPurchaseDate(date);
            purchaseDetailDTO.setPurchaseDate(date);

            purchaseOrderEntity.setStatus(0);
            purchaseDetailDTO.setStatus(0);
            // Save purchase order
            purchaseOrderRepository.save(purchaseOrderEntity);
            // Save purchase details
            purchaseDetailDTO.setPurchaseItems(new LinkedList<>());
            for (CreatePurchaseOrderDTO.PurchaseItem item:
                    createPurchaseOrderDTO.getPurchaseItems()) {
                PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                // Purchase detail's basic info
                purchaseDetail.setPurchaseOrderId(purchaseOrderId);
                purchaseDetail.setProductId(item.getProductId());
                purchaseDetail.setQuantity(item.getQuantity());
                // Historical data
                Optional<ProductEntity> productOptional = productRepository.findById(item.getProductId());
                if (productOptional.isPresent()) {
                    purchaseDetail.setProductName(productOptional.get().getName());
                    purchaseDetail.setProductPrice(productOptional.get().getPrice());
                } else {
                    purchaseManagementDTO.setSuccess(false);
                    purchaseManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
                    return purchaseManagementDTO;
                }
                // Save purchase detail
                purchaseDetailDTO.getPurchaseItems().add(purchaseDetail);
                purchaseDetailRepository.save(purchaseDetail);
            }
            purchaseManagementDTO.setSuccess(true);
            purchaseManagementDTO.setPurchaseDetail(purchaseDetailDTO);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }
}
