package com.group3.apiserver.service;

import com.group3.apiserver.dto.sender.PaginationDTO;
import com.group3.apiserver.dto.sender.product.ProductDetailDTO;
import com.group3.apiserver.dto.sender.product.ProductListItemDTO;
import com.group3.apiserver.dto.sender.FileProcessingDTO;
import com.group3.apiserver.dto.sender.product.ProductManagementDTO;
import com.group3.apiserver.entity.ProductDescriptionEntity;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.entity.ProductPhotographEntity;
import com.group3.apiserver.message.ErrorMessage;
import com.group3.apiserver.repository.ProductDescriptionRepository;
import com.group3.apiserver.repository.ProductPhotographRepository;
import com.group3.apiserver.repository.ProductRepository;
import com.group3.apiserver.util.AuthenticationUtil;
import com.group3.apiserver.util.FileProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private ProductPhotographRepository productPhotographRepository;
    private ProductDescriptionRepository productDescriptionRepository;
    private AuthenticationUtil authenticationUtil;
    private FileProcessingUtil fileProcessingUtil;

    @Autowired
    public void setFileProcessingUtil(FileProcessingUtil fileProcessingUtil) {
        this.fileProcessingUtil = fileProcessingUtil;
    }

    @Autowired
    public void setAuthenticationUtil(AuthenticationUtil authenticationUtil) {
        this.authenticationUtil = authenticationUtil;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProductPhotographRepository(ProductPhotographRepository productPhotographRepository) {
        this.productPhotographRepository = productPhotographRepository;
    }

    @Autowired
    public void setProductDescriptionRepository(ProductDescriptionRepository productDescriptionRepository) {
        this.productDescriptionRepository = productDescriptionRepository;
    }

    public PaginationDTO<ProductListItemDTO> findProducts(Integer page,
                                                          String key,
                                                          String category,
                                                          Integer orderBy) {
        Pageable pageable = PageRequest.of(page - 1,
                8, orderBy>0?Sort.by("price"):Sort.by("price").descending());
        Page<ProductEntity> productPage =
                productRepository.findAllByNameLikeAndCategoryLike("%" + key + "%", category, pageable);
        // Check if vendor search product by product id
        Optional<ProductEntity> productFindByIdOptional = Optional.empty();
        try {
            productFindByIdOptional = productRepository.findById(Integer.valueOf(key));
        } catch (Exception ignored) {}

        PaginationDTO<ProductListItemDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setCurrentPage(page);
        paginationDTO.setTotalPages(productPage.getTotalPages());
        paginationDTO.setItemList(new LinkedList<>());
        // If get the exist product by product id, then only return it
        if (productFindByIdOptional.isPresent()) {
            paginationDTO.getItemList().add(covertToProductListItemDTO(productFindByIdOptional.get()));
        } else {
            for (ProductEntity product :
                    productPage.toList()) {
                paginationDTO.getItemList().add(covertToProductListItemDTO(product));
            }
        }
        return paginationDTO;
    }

    private ProductListItemDTO covertToProductListItemDTO(ProductEntity product) {
        ProductListItemDTO productListItemDTO = new ProductListItemDTO();
        productListItemDTO.setId(product.getId());
        productListItemDTO.setName(product.getName());
        productListItemDTO.setCategory(product.getCategory());
        productListItemDTO.setPrice(product.getPrice());
        productListItemDTO.setOutOfStock(product.getOutOfStock());
        productListItemDTO.setRating(product.getTotalComments() != 0?
                Double.parseDouble(product.getTotalStars().toString())
                        / Double.parseDouble(product.getTotalComments().toString()):0);
        productListItemDTO.setThumbnailLocation(product.getThumbnailLocation());
        return productListItemDTO;
    }

    public ProductDetailDTO findProduct(Integer id) {
        Optional<ProductEntity> productEntityOptional =productRepository.findById(id);
        ProductDetailDTO productDetailDTO = null;
        if (productEntityOptional.isPresent()) {
            ProductEntity product = productEntityOptional.get();
            productDetailDTO = new ProductDetailDTO(product);
            productDetailDTO.setRating(product.getTotalComments() != 0?
                    Double.parseDouble(product.getTotalStars().toString())
                            / Double.parseDouble(product.getTotalComments().toString()):0);
            productDetailDTO.setProductPhotographs(productPhotographRepository.findAllByProductId(productDetailDTO.getId()));
            productDetailDTO.setProductDescriptions(productDescriptionRepository.findAllByProductId(productDetailDTO.getId()));
        }
        return productDetailDTO;
    }

    public FileProcessingDTO uploadThumbnail(MultipartFile thumbnail,
                                             Integer userId,
                                             Integer productId,
                                             String token) {
        FileProcessingDTO fileProcessingDTO = new FileProcessingDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                String thumbnailLocation;
                // Save file to local
                try {
                    thumbnailLocation = fileProcessingUtil.saveFile(thumbnail);
                } catch (IOException e) {
                    e.printStackTrace();
                    fileProcessingDTO.setSuccess(false);
                    return fileProcessingDTO;
                }
                // Save file link to database
                ProductEntity product = productOptional.get();
                product.setThumbnailLocation(thumbnailLocation);
                productRepository.save(product);
                fileProcessingDTO.setSuccess(true);
                fileProcessingDTO.setFileLink(thumbnailLocation);
            } else {
                fileProcessingDTO.setSuccess(false);
                fileProcessingDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            fileProcessingDTO.setSuccess(false);
            fileProcessingDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return fileProcessingDTO;
    }

    public FileProcessingDTO deleteThumbnail(Integer userId,
                                             String token,
                                             Integer productId) {
        FileProcessingDTO fileProcessingDTO = new FileProcessingDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                ProductEntity product = productOptional.get();
                String fileName = product.getThumbnailLocation();
                fileName = fileName.substring(fileName.indexOf("/static/") + 8);
                if (!fileProcessingUtil.deleteFile(fileName)) {
                    fileProcessingDTO.setSuccess(false);
                    return fileProcessingDTO;
                }
                product.setThumbnailLocation("");
                productRepository.save(product);
                fileProcessingDTO.setSuccess(true);
            } else {
                fileProcessingDTO.setSuccess(false);
                fileProcessingDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            fileProcessingDTO.setSuccess(false);
            fileProcessingDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return fileProcessingDTO;
    }

    public FileProcessingDTO uploadPhotograph(MultipartFile photograph,
                                              Integer userId,
                                              Integer productId,
                                              String token) {
        FileProcessingDTO fileProcessingDTO = new FileProcessingDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                String photographLocation;
                try {
                    photographLocation = fileProcessingUtil.saveFile(photograph);
                } catch (IOException e) {
                    e.printStackTrace();
                    fileProcessingDTO.setSuccess(false);
                    return fileProcessingDTO;
                }
                ProductPhotographEntity productPhotograph = new ProductPhotographEntity();
                productPhotograph.setProductId(productId);
                productPhotograph.setFileLocation(photographLocation);
                productPhotograph = productPhotographRepository.save(productPhotograph);
                fileProcessingDTO.setSuccess(true);
                fileProcessingDTO.setFileLink(photographLocation);
                fileProcessingDTO.setFileId(productPhotograph.getId());
            } else {
                fileProcessingDTO.setSuccess(false);
                fileProcessingDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            fileProcessingDTO.setSuccess(false);
            fileProcessingDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return fileProcessingDTO;
    }

    public FileProcessingDTO deletePhotograph(Integer userId,
                                              String token,
                                              Integer productId,
                                              Integer photographId) {
        FileProcessingDTO fileProcessingDTO = new FileProcessingDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Optional<ProductPhotographEntity> productPhotographOptional
                        = productPhotographRepository.findById(photographId);
                if (productPhotographOptional.isPresent()) {
                    ProductPhotographEntity productPhotograph = productPhotographOptional.get();
                    String fileName = productPhotograph.getFileLocation();
                    fileName = fileName.substring(fileName.indexOf("/static/") + 8);
                    if (!fileProcessingUtil.deleteFile(fileName)) {
                        fileProcessingDTO.setSuccess(false);
                        return fileProcessingDTO;
                    }
                    productPhotographRepository.deleteById(photographId);
                    fileProcessingDTO.setSuccess(true);
                } else {
                    fileProcessingDTO.setSuccess(false);
                }
            } else {
                fileProcessingDTO.setSuccess(false);
                fileProcessingDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            fileProcessingDTO.setSuccess(false);
            fileProcessingDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return fileProcessingDTO;
    }

    public ProductManagementDTO modifyProductBasicInfo(Integer userId,
                                                       String token,
                                                       String name,
                                                       String category,
                                                       Double price) {
        ProductManagementDTO productManagementDTO = new ProductManagementDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            ProductEntity product = new ProductEntity();
            product.setName(name);
            product.setCategory(category);
            product.setPrice(BigDecimal.valueOf(price));
            product.setOutOfStock(false);
            product.setThumbnailLocation("");
            product.setTotalStars(0);
            product.setTotalComments(0);
            product = productRepository.save(product);
            productManagementDTO.setSuccess(true);
            productManagementDTO.addProductDetailDTO(product.getId());
        } else {
            productManagementDTO.setSuccess(false);
            productManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return productManagementDTO;
    }

    public ProductManagementDTO modifyProductBasicInfo(Integer userId,
                                                       String token,
                                                       Integer productId,
                                                       String name,
                                                       String category,
                                                       Double price) {
        ProductManagementDTO productManagementDTO = new ProductManagementDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                ProductEntity product = productOptional.get();
                product.setName(name);
                product.setCategory(category);
                product.setPrice(BigDecimal.valueOf(price));
                productRepository.save(product);
                productManagementDTO.setSuccess(true);
                productManagementDTO.addProductDetailDTO(name, category, price);
            } else {
                productManagementDTO.setSuccess(false);
                productManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            productManagementDTO.setSuccess(false);
            productManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return productManagementDTO;
    }

    public ProductManagementDTO addDescription(Integer userId,
                                               String token,
                                               Integer productId,
                                               List<ProductDescriptionEntity> productDescriptions) {
        ProductManagementDTO productManagementDTO = new ProductManagementDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                for (ProductDescriptionEntity productDescription :
                        productDescriptions) {
                    productDescriptionRepository.save(productDescription);
                }
                productManagementDTO.setSuccess(true);
            } else {
                productManagementDTO.setSuccess(false);
                productManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            productManagementDTO.setSuccess(false);
            productManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return productManagementDTO;
    }

    public ProductManagementDTO modifyDescription(Integer userId,
                                                  String token,
                                                  Integer productId,
                                                  List<ProductDescriptionEntity> productDescriptions) {
        ProductManagementDTO productManagementDTO = new ProductManagementDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                for (ProductDescriptionEntity productDescription :
                        productDescriptions) {
                    if (productDescription.getId() != 0) {
                        productDescriptionRepository.save(productDescription);
                    }
                }
                productManagementDTO.setSuccess(true);
            } else {
                productManagementDTO.setSuccess(false);
                productManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            productManagementDTO.setSuccess(false);
            productManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return productManagementDTO;
    }

    public ProductManagementDTO deleteDescription(Integer userId,
                                                  String token,
                                                  Integer productId,
                                                  Integer descriptionId) {
        ProductManagementDTO productManagementDTO = new ProductManagementDTO();
        if (authenticationUtil.vendorAuthentication(userId, token)) {
            Optional<ProductEntity> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                productDescriptionRepository.deleteById(descriptionId);
                productManagementDTO.setSuccess(true);
            } else {
                productManagementDTO.setSuccess(false);
                productManagementDTO.setMessage(ErrorMessage.PRODUCT_NOT_FOUND);
            }
        } else {
            productManagementDTO.setSuccess(false);
            productManagementDTO.setMessage(ErrorMessage.AUTHENTICATION_FAIL);
        }
        return productManagementDTO;
    }
}