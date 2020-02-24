package com.group3.apiserver.service;

import com.group3.apiserver.dto.*;
import com.group3.apiserver.entity.*;
import com.group3.apiserver.message.ErrorMessage;
import com.group3.apiserver.repository.*;
import com.group3.apiserver.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {
    private UserRepository userRepository;
    private ShoppingCartItemRepository shoppingCartItemRepository;
    private ProductRepository productRepository;
    private AuthenticationUtil authenticationUtil;
    private PurchaseDetailRepository purchaseDetailRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private ReviewRepository reviewRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setShoppingCartItemRepository(ShoppingCartItemRepository shoppingCartItemRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setAuthenticationUtil(AuthenticationUtil authenticationUtil) {
        this.authenticationUtil = authenticationUtil;
    }

    @Autowired
    public void setPurchaseDetailRepository(PurchaseDetailRepository purchaseDetailRepository) {
        this.purchaseDetailRepository = purchaseDetailRepository;
    }

    @Autowired
    public void setPurchaseOrderRepository(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Autowired
    public void setReviewRepository(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public UserManagementDTO creatUser(String email, String pwd, String name, String shippingAddr) {
        UserManagementDTO creatUserDTO = new UserManagementDTO();
        // Check if email has been used.
        if (userRepository.findByEmail(email).isPresent()) {
            creatUserDTO.setSuccess(false);
            creatUserDTO.setMessage(ErrorMessage.EMAIL_VALIDATION_FAIL);
        } else {
            String validator = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08" +
                    "\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?" +
                    ":(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][" +
                    "0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x0" +
                    "1-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
            // Validate user's email address
            if (Pattern.matches(validator, email)) {
                UserEntity user = new UserEntity();
                user.setEmail(email);
                user.setPwd(pwd);
                user.setName(name);
                user.setShippingAddr(shippingAddr);
                creatUserDTO.setSuccess(true);
                // Generate token and create time
                creatUserDTO.setToken(setTokenAndCreateTime(user));
                userRepository.save(user);
                // Get auto generated id from database and set to DTO
                Optional<UserEntity> userOptional = userRepository.findByEmail(user.getEmail());
                userOptional.ifPresent(u -> {
                    creatUserDTO.setId(u.getId());
                    creatUserDTO.setName(u.getName());
                });
            } else {
                creatUserDTO.setSuccess(false);
                creatUserDTO.setMessage(ErrorMessage.EMAIL_VALIDATION_FAIL);
            }
        }
        return creatUserDTO;
    }

    public UserManagementDTO login(String email, String pwd) {
        UserManagementDTO loginDTO = new UserManagementDTO();
        // Find user by it's email
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // Validate user's pwd
            if (Objects.equals(pwd, user.getPwd())) {
                loginDTO.setSuccess(true);
                // Generate token and create time
                loginDTO.setToken(setTokenAndCreateTime(user));
                user = userRepository.save(user);
                loginDTO.setId(user.getId());
                loginDTO.setName(user.getName());
            } else {
                loginDTO.setSuccess(false);
            }
        } else {
            loginDTO.setSuccess(false);
        }
        return loginDTO;
    }

    public UserManagementDTO logout(Integer id) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's user id
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setToken(null);
            user.setCreateTime(null);
            userRepository.save(user);
            userManagementDTO.setSuccess(true);
        } else {
            userManagementDTO.setSuccess(false);
            userManagementDTO.setMessage(ErrorMessage.USER_NOT_FOUND);
        }
        return userManagementDTO;
    }

    public UserManagementDTO changePwd(Integer id, String oldPwd, String newPwd) {
        UserManagementDTO userManagementDTO = new UserManagementDTO();
        // Find user by it's user id
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // Validate user's password
            if (Objects.equals(oldPwd, user.getPwd())) {
                // Set new password
                user.setPwd(newPwd);
                userManagementDTO.setSuccess(true);
                userManagementDTO.setToken(setTokenAndCreateTime(user));
                user = userRepository.save(user);
                userManagementDTO.setId(user.getId());
            } else {
                userManagementDTO.setSuccess(false);
                userManagementDTO.setMessage(ErrorMessage.WRONG_PASSWORD);
            }
        } else {
            userManagementDTO.setSuccess(false);
            userManagementDTO.setMessage(ErrorMessage.USER_NOT_FOUND);
        }
        return userManagementDTO;
    }

    private String setTokenAndCreateTime(UserEntity user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setToken(token);
        user.setCreateTime(Long.toString(System.currentTimeMillis()));
        return token;
    }

    public ShoppingCartManagementDTO modifyShoppingCartItem(Integer userId, Integer productId, Integer quantity, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        // Authenticate user
        if (authenticationUtil.userAuthentication(userId, token)) {
            ShoppingCartItemEntity shoppingCartItem = new ShoppingCartItemEntity();
            shoppingCartItem.setUserId(userId);
            // Check if there is really has a product with this id
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                // Save changes into database
                shoppingCartItem.setProductId(productId);
                shoppingCartItem.setQuantity(quantity);
                shoppingCartItemRepository.save(shoppingCartItem);
                // Get updated shopping cart items from database
                for (ShoppingCartItemEntity e :
                        shoppingCartItemRepository.findAllByUserId(userId)) {
                    // The newest inserted one would be null if don't use this way
                    if (e.getProductId() == productOptional.get().getId()) {
                        shoppingCartManagementDTO.addShoppingCartItemDTO(productOptional.get(), e.getQuantity());
                    } else {
                        shoppingCartManagementDTO.addShoppingCartItemDTO(e.getProduct(), e.getQuantity());
                    }
                }
                shoppingCartManagementDTO.setSuccess(true);
                return shoppingCartManagementDTO;
            } else {
                shoppingCartManagementDTO.setSuccess(false);
                shoppingCartManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }

    public ShoppingCartManagementDTO getShoppingCartItems(Integer userId, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        if (authenticationUtil.userAuthentication(userId, token)) {
            for (ShoppingCartItemEntity e :
                    shoppingCartItemRepository.findAllByUserId(userId)) {
                shoppingCartManagementDTO.addShoppingCartItemDTO(e.getProduct(), e.getQuantity());
            }
            shoppingCartManagementDTO.setSuccess(true);
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }

    public ShoppingCartManagementDTO deleteShoppingCartItem(Integer userId, Integer productId, String token) {
        ShoppingCartManagementDTO shoppingCartManagementDTO = new ShoppingCartManagementDTO();
        if (authenticationUtil.userAuthentication(userId, token)) {
            ShoppingCartItemEntityPK shoppingCartItemPK = new ShoppingCartItemEntityPK();
            shoppingCartItemPK.setProductId(productId);
            shoppingCartItemPK.setUserId(userId);
            if (shoppingCartItemRepository.findById(shoppingCartItemPK).isPresent()) {
                shoppingCartItemRepository.deleteById(shoppingCartItemPK);
                shoppingCartManagementDTO.setSuccess(true);
            } else {
                shoppingCartManagementDTO.setSuccess(false);
                shoppingCartManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            shoppingCartManagementDTO.setSuccess(false);
            shoppingCartManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return shoppingCartManagementDTO;
    }

    @Transactional
    public ReviewManagementDTO saveUserReview(Integer purchaseOrderId, Integer productId, String token, Integer stars, String content) {
        ReviewManagementDTO reviewManagementDTO = new ReviewManagementDTO();
        // Try to find target purchase order
        Optional<PurchaseOrderEntity> purchaseOrderOptional = purchaseOrderRepository.findById(purchaseOrderId);
        if (purchaseOrderOptional.isPresent()) {
            // Check if purchase order's status is "shipped"
            if (purchaseOrderOptional.get().getStatus() == 2) {
                Optional<UserEntity> userOptional = userRepository.findById(purchaseOrderOptional.get().getUserId());
                userOptional.ifPresent(user -> {
                    // User authentication
                    if (authenticationUtil.userAuthentication(user.getId(), token)) {
                        PurchaseDetailEntityPK purchaseDetailPK = new PurchaseDetailEntityPK();
                        purchaseDetailPK.setPurchaseOrderId(purchaseOrderId);
                        purchaseDetailPK.setProductId(productId);
                        Optional<PurchaseDetailEntity> purchaseDetailOptional
                                = purchaseDetailRepository.findById(purchaseDetailPK);
                        // Try to find target purchase detail
                        if (purchaseDetailOptional.isPresent()) {
                            // Generate review
                            ReviewEntity review = new ReviewEntity();
                            review.setPurchaseOrderId(purchaseOrderId);
                            review.setProductId(productId);
                            review.setStars(Math.min(Math.abs(stars), 5));
                            review.setContent(content);
                            review.setCommentDate(Long.toString(System.currentTimeMillis()));
                            // Modify product's rating
                            Optional<ProductEntity> productOptional = productRepository.findById(productId);
                            // Remove the affect of old review
                            int oldStars;
                            ReviewEntityPK reviewPK = new ReviewEntityPK();
                            reviewPK.setProductId(productId);
                            reviewPK.setPurchaseOrderId(purchaseOrderId);
                            if (reviewRepository.findById(reviewPK).isPresent()) {
                                oldStars = reviewRepository.findById(reviewPK).get().getStars();
                                if (productOptional.isPresent()) {
                                    ProductEntity product = productOptional.get();
                                    product.setTotalStars(product.getTotalStars() - oldStars);
                                    product.setTotalComments(product.getTotalComments() - 1);
                                    productRepository.save(product);
                                }
                            }
                            // Add affect of new review
                            if (productOptional.isPresent()) {
                                ProductEntity product = productOptional.get();
                                product.setTotalStars(product.getTotalStars() + stars);
                                product.setTotalComments(product.getTotalComments() + 1);
                                productRepository.save(product);
                            }
                            reviewRepository.save(review);
                            // Save review
                            reviewManagementDTO.setSuccess(true);
                            reviewManagementDTO.setProductId(productId);
                        } else {
                            reviewManagementDTO.setSuccess(false);
                            reviewManagementDTO.setMessage(ErrorMessage.NO_SUCH_PRODUCT_FOUND_IN_THIS_PURCHASE_ORDER);
                        }
                    } else {
                        reviewManagementDTO.setSuccess(false);
                        reviewManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
                    }
                });
            } else {
                reviewManagementDTO.setSuccess(false);
                reviewManagementDTO.setMessage(ErrorMessage.CANNOT_COMMENT_UNSHIPPED_PRODUCT);
            }
        } else {
            reviewManagementDTO.setSuccess(false);
            reviewManagementDTO.setMessage(ErrorMessage.PURCHASE_ORDER_NOT_FOUND);
        }
        return reviewManagementDTO;
    }

    public PaginationDTO<ReviewDTO> getReviews(Integer productId, Integer page) {
        PaginationDTO<ReviewDTO> paginationDTO = new PaginationDTO<>();
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<ReviewEntity> reviewPages = reviewRepository.findAllByProductId(productId, pageable);
        // Construct paginationDTO
        paginationDTO.setCurrentPage(page);
        paginationDTO.setTotalPages(reviewPages.getTotalPages());
        paginationDTO.setItemList(new LinkedList<>());
        for (ReviewEntity review :
                reviewPages) {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setStars(review.getStars());
            reviewDTO.setContent(review.getContent());
            reviewDTO.setCommentDate(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                            Double.valueOf(review.getCommentDate())));

            purchaseOrderRepository.findById(review.getPurchaseOrderId()).flatMap(purchaseOrderEntity ->
                    userRepository.findById(purchaseOrderEntity.getUserId())).ifPresent(userEntity ->
                        reviewDTO.setUserName(userEntity.getName()));
            paginationDTO.getItemList().add(reviewDTO);
        }
        return paginationDTO;
    }
}