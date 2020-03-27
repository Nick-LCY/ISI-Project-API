package com.group3.apiserver.service;

import com.group3.apiserver.dto.receiver.purchaseorder.CreatePurchaseOrderDTO;
import com.group3.apiserver.dto.sender.purchaseorder.PurchaseManagementDTO;
import com.group3.apiserver.dto.sender.purchaseorder.list.PurchaseOrderListDTO;
import com.group3.apiserver.dto.sender.purchaseorder.detail.PurchaseDetailDTO;
import com.group3.apiserver.dto.sender.purchaseorder.detail.PurchaseOrderDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.PurchaseDetailEntity;
import com.group3.apiserver.entity.PurchaseOrderEntity;
import com.group3.apiserver.entity.UserEntity;
import com.group3.apiserver.message.ErrorMessage;
import com.group3.apiserver.repository.*;
import com.group3.apiserver.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PurchaseOrderService {
    private final String[] STATUS_LIST = {"pending", "hold", "shipped", "cancelled"};

    private AuthenticationUtil authenticationUtil;
    private PurchaseDetailRepository purchaseDetailRepository;
    private ProductRepository productRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private UserRepository userRepository;
    private ShoppingCartItemRepository shoppingCartItemRepository;

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

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setShoppingCartItemRepository(ShoppingCartItemRepository shoppingCartItemRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    @Transactional
    public PurchaseManagementDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        if (authenticationUtil.userAuthentication(createPurchaseOrderDTO.getUserId(), createPurchaseOrderDTO.getToken())) {
            PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
            PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity();
            // Generate a unique PoNo
            Random random = new Random();
            int purchaseOrderId = random.nextInt(90000000) + 10000000;
            while (purchaseOrderRepository.findById(purchaseOrderId).isPresent()) {
                purchaseOrderId = random.nextInt(90000000) + 10000000;
            }
            // Purchase order's basic info
            purchaseOrder.setId(purchaseOrderId);
            purchaseOrderDTO.setPurchaseOrderId(purchaseOrderId);

            purchaseOrder.setUserId(createPurchaseOrderDTO.getUserId());

            long timestamp = System.currentTimeMillis();
            purchaseOrder.setPurchaseDate(Long.toString(timestamp));
            purchaseOrderDTO.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp));

            purchaseOrder.setStatus(0);
            purchaseOrderDTO.setStatusInString(STATUS_LIST[0]);

            // Temporary set 0
            purchaseOrder.setTotalAmount(BigDecimal.valueOf(0));
            // Save purchase order
            purchaseOrderRepository.save(purchaseOrder);
            // Save purchase details
            BigDecimal totalAmount = BigDecimal.valueOf(0);
            purchaseOrderDTO.setPurchaseItems(new LinkedList<>());
            for (CreatePurchaseOrderDTO.PurchaseItem item:
                    createPurchaseOrderDTO.getPurchaseItems()) {
                PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                PurchaseDetailDTO purchaseDetailDTO = new PurchaseDetailDTO();
                // Purchase detail's basic info
                purchaseDetail.setPurchaseOrderId(purchaseOrderId);

                purchaseDetail.setProductId(item.getId());
                purchaseDetailDTO.setProductId(item.getId());

                purchaseDetail.setQuantity(item.getQuantity());
                purchaseDetailDTO.setQuantity(item.getQuantity());
                // Historical data
                Optional<ProductEntity> productOptional = productRepository.findById(item.getId());
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
                purchaseOrder.setTotalAmount(totalAmount);
                purchaseOrderDTO.getPurchaseItems().add(purchaseDetailDTO);
                purchaseDetailRepository.save(purchaseDetail);
            }
            // Update purchase order
            purchaseOrderDTO.setTotalAmount(totalAmount);
            purchaseOrder.setTotalAmount(totalAmount);
            purchaseOrderRepository.save(purchaseOrder);

            // Clean shopping cart items
            shoppingCartItemRepository.deleteAll(shoppingCartItemRepository.findAllByUserId(createPurchaseOrderDTO.getUserId()));

            purchaseManagementDTO.setSuccess(true);
            purchaseManagementDTO.setPurchaseDetail(purchaseOrderDTO);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }

    public PurchaseManagementDTO updatePurchaseOrderStatus(Integer userId, String token, Integer purchaseOrderId, String statusInString) {
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        int status = 0;
        for (int i = 0; i < 4; i++) {
            if (STATUS_LIST[i].equals(statusInString)) {
                status = i;
            }
        }
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

    public PurchaseManagementDTO getPurchaseOrders(Integer userId, String token) {
        // Init needed DTOs
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        purchaseManagementDTO.setPurchaseOrderListDTOList(new LinkedList<>());
        // User authentication
        if (authenticationUtil.userAuthentication(userId, token)) {
            boolean isVendor = authenticationUtil.vendorAuthentication(userId, token);
            // If is vendor, get all purchase orders
            List<PurchaseOrderEntity> purchaseOrderList = isVendor
                    ?purchaseOrderRepository.findAll()
                    :purchaseOrderRepository.findAllByUserId(userId);
            // Convert PurchaseOrder to PurchaseOrderDTO and construct paginationDTO
            for (PurchaseOrderEntity purchaseOrder :
                    purchaseOrderList) {
                PurchaseOrderListDTO purchaseOrderListDTO = new PurchaseOrderListDTO();
                purchaseOrderListDTO.setPurchaseOrderId(purchaseOrder.getId());
                purchaseOrderListDTO.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Double.valueOf(purchaseOrder.getPurchaseDate())));
                purchaseOrderListDTO.setStatusInString(STATUS_LIST[purchaseOrder.getStatus()]);
                purchaseOrderListDTO.setTotalAmount(purchaseOrder.getTotalAmount());
                assert userRepository.findById(purchaseOrder.getUserId()).isPresent();
                purchaseOrderListDTO.setCustomerName(isVendor?userRepository.findById(purchaseOrder.getUserId()).get().getName():null);
                purchaseManagementDTO.getPurchaseOrderListDTOList().add(purchaseOrderListDTO);
            }
            // Construct purchaseManagementDTO
            purchaseManagementDTO.setSuccess(true);
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }

    public PurchaseManagementDTO getPurchaseOrder(Integer userId, String token, Integer purchaseOrderId) {
        PurchaseManagementDTO purchaseManagementDTO = new PurchaseManagementDTO();
        // User authentication
        if (authenticationUtil.userAuthentication(userId, token)) {
            // Try to find the purchase order
            Optional<PurchaseOrderEntity> purchaseOrderOptional = purchaseOrderRepository.findById(purchaseOrderId);
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            UserEntity user = userOptional.orElse(new UserEntity());
            if (purchaseOrderOptional.isPresent()) {
                PurchaseOrderEntity purchaseOrder = purchaseOrderOptional.get();
                if (!authenticationUtil.vendorAuthentication(userId, token) && purchaseOrder.getUserId() != userId) {
                    purchaseManagementDTO.setSuccess(false);
                    purchaseManagementDTO.setMessage(ErrorMessage.HAVE_NO_RIGHT);
                    return purchaseManagementDTO;
                }
                List<PurchaseDetailEntity> purchaseDetails = purchaseDetailRepository.findAllByPurchaseOrderId(purchaseOrderId);
                PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
                // Basic purchase order information
                purchaseOrderDTO.setPurchaseOrderId(purchaseOrderId);
                if (purchaseOrder.getPurchaseDate() != null && !purchaseOrder.getPurchaseDate().isEmpty()) {
                    purchaseOrderDTO.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Double.valueOf(purchaseOrder.getPurchaseDate())));
                }
                if (purchaseOrder.getShipmentDate() != null && !purchaseOrder.getShipmentDate().isEmpty()) {
                    purchaseOrderDTO.setShipmentDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Double.valueOf(purchaseOrder.getShipmentDate())));
                }
                if (purchaseOrder.getCancelDate() != null && !purchaseOrder.getCancelDate().isEmpty()) {
                    purchaseOrderDTO.setCancelDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Double.valueOf(purchaseOrder.getCancelDate())));
                }
                // 0 represents user, 1 represents vendor
                purchaseOrderDTO.setCancelledBy(purchaseOrder.getCancelledBy() == null ?
                        null :
                        purchaseOrder.getCancelledBy() == 1 ?
                                "vendor" :
                                user.getName());
                purchaseOrderDTO.setStatusInString(STATUS_LIST[purchaseOrder.getStatus()]);
                purchaseOrderDTO.setTotalAmount(purchaseOrder.getTotalAmount());
                purchaseOrderDTO.setCustomerName(user.getName());
                purchaseOrderDTO.setShippingAddress(user.getShippingAddr());
                // Purchase order detail information
                purchaseOrderDTO.setPurchaseItems(new LinkedList<>());
                for (PurchaseDetailEntity purchaseDetail :
                        purchaseDetails) {
                    PurchaseDetailDTO purchaseDetailDTO = new PurchaseDetailDTO();
                    purchaseDetailDTO.setProductId(purchaseDetail.getProductId());
                    purchaseDetailDTO.setProductName(purchaseDetail.getProductName());
                    purchaseDetailDTO.setProductPrice(purchaseDetail.getProductPrice());
                    purchaseDetailDTO.setQuantity(purchaseDetail.getQuantity());
                    purchaseOrderDTO.getPurchaseItems().add(purchaseDetailDTO);
                }

                purchaseManagementDTO.setSuccess(true);
                purchaseManagementDTO.setPurchaseDetail(purchaseOrderDTO);
            } else {
                purchaseManagementDTO.setSuccess(false);
                purchaseManagementDTO.setMessage(ErrorMessage.PURCHASE_ORDER_NOT_FOUND);
                return purchaseManagementDTO;
            }
        } else {
            purchaseManagementDTO.setSuccess(false);
            purchaseManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return purchaseManagementDTO;
    }
}
