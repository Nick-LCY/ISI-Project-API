package com.group3.apiserver.service;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.purchase.CreatePurchaseOrderDTO;
import com.group3.apiserver.dto.purchase.PurchaseManagementDTO;
import com.group3.apiserver.dto.purchase.PurchaseOrderDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseDetailDTO;
import com.group3.apiserver.dto.purchase.items.PurchaseItemsDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.PurchaseDetailEntity;
import com.group3.apiserver.entity.PurchaseOrderEntity;
import com.group3.apiserver.message.ErrorMessage;
import com.group3.apiserver.repository.ProductRepository;
import com.group3.apiserver.repository.PurchaseDetailRepository;
import com.group3.apiserver.repository.PurchaseOrderRepository;
import com.group3.apiserver.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

    @Transactional
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
            purchaseItemsDTO.setPoNo(purchaseOrderId);

            purchaseOrderEntity.setUserId(createPurchaseOrderDTO.getUserId());

            double timestamp = System.currentTimeMillis();
            purchaseOrderEntity.setPurchaseDate(BigDecimal.valueOf(System.currentTimeMillis()).toString());
            purchaseItemsDTO.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp));

            purchaseOrderEntity.setStatus(0);
            purchaseItemsDTO.setStatus(0);

            // Temporary set 0
            purchaseOrderEntity.setTotalAmount(BigDecimal.valueOf(0));
            // Save purchase order
            purchaseOrderRepository.save(purchaseOrderEntity);
            // Save purchase details
            BigDecimal totalAmount = BigDecimal.valueOf(0);
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

                    totalAmount = totalAmount.add(productOptional.get().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                } else {
                    purchaseManagementDTO.setSuccess(false);
                    purchaseManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
                    return purchaseManagementDTO;
                }
                // Save purchase detail
                purchaseOrderEntity.setTotalAmount(totalAmount);
                purchaseItemsDTO.getPurchaseItems().add(purchaseDetailDTO);
                purchaseDetailRepository.save(purchaseDetail);
            }
            // Update purchase order
            purchaseItemsDTO.setTotalAmount(totalAmount);
            purchaseOrderEntity.setTotalAmount(totalAmount);
            purchaseOrderRepository.save(purchaseOrderEntity);

            purchaseManagementDTO.setSuccess(true);
            purchaseManagementDTO.setPurchaseDetail(purchaseItemsDTO);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }

    public PurchaseManagementDTO updatePurchaseOrderStatus(Integer userId, String token, Integer purchaseOrderId, Integer status) {
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        // User authentication
        if (authenticationUtil.userAuthentication(userId, token)) {
            Optional<PurchaseOrderEntity> purchaseOrderOptional = purchaseOrderRepository.findById(purchaseOrderId);
            PurchaseOrderEntity purchaseOrder;
            // Try to find the purchase order
            if (purchaseOrderOptional.isPresent()) {
                purchaseOrder = purchaseOrderOptional.get();
                if (purchaseOrder.getUserId() != userId && !authenticationUtil.vendorAuthentication(userId, token)) {
                    purchaseManagementDTO.setSuccess(false);
                    purchaseManagementDTO.setMessage(ErrorMessage.HAVE_NO_RIGHT);
                } else {
                    if (purchaseOrder.getStatus() > 1) {
                        // If status now is bigger than 1 (i.e. 2 or 3)
                        // That means it's in status cancelled or shipped
                        // So, no operations are allowed
                        purchaseManagementDTO.setSuccess(false);
                        purchaseManagementDTO.setMessage(ErrorMessage.CANNOT_CHANGE_SHIPPED_AND_CANCELLED);
                    } else if (purchaseOrder.getStatus() >= status) {
                        // If new status is bigger than old status
                        // It should be belonged to one of these situations
                        // 1: pending -> pending; 2: hold -> pending; 3: hold -> hold
                        // So, these operations are not allowed.
                        purchaseManagementDTO.setSuccess(false);
                        purchaseManagementDTO.setMessage(ErrorMessage.CANNOT_CHANGE_TO_TARGET_STATUS);
                    } else {
                        // Now start to process base on different status
                        if (status == 1 || status == 2) {
                            if (authenticationUtil.vendorAuthentication(userId, token)) {
                                purchaseOrder.setStatus(status);
                                if (status == 2) {
                                    purchaseOrder.setShipmentDate(BigDecimal.valueOf(System.currentTimeMillis()).toString());
                                }
                                purchaseOrderRepository.save(purchaseOrder);
                                purchaseManagementDTO.setSuccess(true);
                            } else {
                                purchaseManagementDTO.setSuccess(false);
                                purchaseManagementDTO.setMessage(ErrorMessage.HAVE_NO_RIGHT);
                            }
                        } else if (status == 3) {
                            purchaseOrder.setStatus(status);
                            // 1 represents cancelled by vendor, 0 represents cancelled by user
                            purchaseOrder.setCancelledBy(
                                    authenticationUtil.vendorAuthentication(userId, token)?1:0);
                            purchaseOrder.setCancelDate(BigDecimal.valueOf(System.currentTimeMillis()).toString());
                            purchaseOrderRepository.save(purchaseOrder);
                            purchaseManagementDTO.setSuccess(true);
                        }
                    }
                }
            } else {
                purchaseManagementDTO.setSuccess(false);
                purchaseManagementDTO.setMessage(ErrorMessage.PURCHASE_ORDER_NOT_FOUND);
            }
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }

    public PurchaseManagementDTO getPurchaseOrders(Integer userId, String token, String statusKey, Integer page) {
        // Init needed DTOs
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        PaginationDTO<PurchaseOrderDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItemList(new LinkedList<>());
        // User authentication
        if (authenticationUtil.userAuthentication(userId, token)) {
            // Select page from database by statusKey
            Page<PurchaseOrderEntity> purchaseOrderPage;
            Pageable pageable = PageRequest.of(page - 1, 10);
            if (statusKey.equals("current")) {
                purchaseOrderPage
                        = purchaseOrderRepository.findAllByUserIdAndTwoStatus(userId, 0, 1, pageable);
            } else if (statusKey.equals("past")) {
                purchaseOrderPage
                        = purchaseOrderRepository.findAllByUserIdAndTwoStatus(userId, 2, 3, pageable);
            } else {
                purchaseManagementDTO.setSuccess(false);
                purchaseManagementDTO.setMessage(ErrorMessage.INVALID_STATUS);
                return purchaseManagementDTO;
            }
            // Convert PurchaseOrder to PurchaseOrderDTO and construct paginationDTO
            paginationDTO.setTotalPages(purchaseOrderPage.getTotalPages());
            paginationDTO.setCurrentPage(page);
            String[] statusList = {"hold", "pending", "shipped", "cancelled"};
            for (PurchaseOrderEntity purchaseOrder :
                    purchaseOrderPage.toList()) {
                PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
                purchaseOrderDTO.setPoNo(purchaseOrder.getId());
                purchaseOrderDTO.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Double.valueOf(purchaseOrder.getPurchaseDate())));
                purchaseOrderDTO.setStatus(statusList[purchaseOrder.getStatus()]);
                purchaseOrderDTO.setTotalAmount(purchaseOrder.getTotalAmount());
                paginationDTO.getItemList().add(purchaseOrderDTO);
            }
            // Construct purchaseManagementDTO
            purchaseManagementDTO.setSuccess(true);
            purchaseManagementDTO.setPoInfo(paginationDTO);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }
}
