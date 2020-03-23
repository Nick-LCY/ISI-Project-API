package com.group3.apiserver.controller;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.ProductDetailDTO;
import com.group3.apiserver.dto.ProductListItemDTO;
import com.group3.apiserver.dto.receiver.product.ManageDescriptionDTO;
import com.group3.apiserver.dto.receiver.product.modifyProductBasicInfoDTO;
import com.group3.apiserver.dto.sender.FileProcessingDTO;
import com.group3.apiserver.dto.sender.ProductManagementDTO;
import com.group3.apiserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("http://localhost:8080")
public class ProductController {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * order_by:
     *  1: ascending
     *  0: descending
     */
    @GetMapping("/products")
    public PaginationDTO<ProductListItemDTO> getProductList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(name = "key", defaultValue = "") String key,
                                                            @RequestParam(name = "category", defaultValue = "____") String category,
                                                            @RequestParam(name = "order_by", defaultValue = "1") Integer orderBy) {
        return productService.findProducts(page, key, category, orderBy);
    }

    @GetMapping("/product")
    public ProductDetailDTO getProduct(@RequestParam(name = "id") Integer id) {
        return productService.findProduct(id);
    }

    @PostMapping("/thumbnail")
    public FileProcessingDTO uploadThumbnail(@RequestParam("thumbnail") MultipartFile thumbnail,
                                             @RequestParam("user_id") Integer userId,
                                             @RequestParam("product_id") Integer productId,
                                             @RequestParam("token") String token) {
        return productService.uploadThumbnail(thumbnail, userId, productId, token);
    }

    @DeleteMapping("/thumbnail")
    public FileProcessingDTO deleteThumbnail(@RequestParam("user_id") Integer userId,
                                             @RequestParam("token") String token,
                                             @RequestParam("product_id") Integer productId) {
        return productService.deleteThumbnail(userId, token, productId);
    }

    @PostMapping("/photograph")
    public FileProcessingDTO uploadPhotograph(@RequestParam("photograph") MultipartFile photograph,
                                              @RequestParam("user_id") Integer userId,
                                              @RequestParam("product_id") Integer productId,
                                              @RequestParam("token") String token) {
        return productService.uploadPhotograph(photograph, userId, productId, token);
    }

    @DeleteMapping("/photograph")
    public FileProcessingDTO deletePhotograph(@RequestParam("user_id") Integer userId,
                                              @RequestParam("token") String token,
                                              @RequestParam("product_id") Integer productId,
                                              @RequestParam("photograph_id") Integer photographId) {
        return productService.deletePhotograph(userId, token, productId, photographId);
    }

    @PostMapping("/product")
    public ProductManagementDTO createProduct(@RequestBody modifyProductBasicInfoDTO createProductParams) {
        return productService.modifyProductBasicInfo(
                createProductParams.getUserId(),
                createProductParams.getToken(),
                createProductParams.getName(),
                createProductParams.getCategory(),
                createProductParams.getPrice()
        );
    }

    @PatchMapping("/product")
    public ProductManagementDTO modifyProduct(@RequestBody modifyProductBasicInfoDTO modifyProductParams) {
        return productService.modifyProductBasicInfo(
                modifyProductParams.getUserId(),
                modifyProductParams.getToken(),
                modifyProductParams.getProductId(),
                modifyProductParams.getName(),
                modifyProductParams.getCategory(),
                modifyProductParams.getPrice()
        );
    }

    @PostMapping("/product_description")
    public ProductManagementDTO addProductDescription(@RequestBody ManageDescriptionDTO addDescriptionParams) {
        return productService.addDescription(
                addDescriptionParams.getUserId(),
                addDescriptionParams.getToken(),
                addDescriptionParams.getProductId(),
                addDescriptionParams.getProductDescriptionsInEntity()
        );
    }

    @PatchMapping("/product_description")
    public ProductManagementDTO modifyProductDescription(@RequestBody ManageDescriptionDTO modifyDescriptionParams) {
        return productService.modifyDescription(
                modifyDescriptionParams.getUserId(),
                modifyDescriptionParams.getToken(),
                modifyDescriptionParams.getProductId(),
                modifyDescriptionParams.getProductDescriptionsInEntity()
        );
    }

    @DeleteMapping("/product_description")
    public ProductManagementDTO deleteProductDescription(@RequestParam("user_id") Integer userId,
                                                         @RequestParam("token") String token,
                                                         @RequestParam("product_id") Integer productId,
                                                         @RequestParam("des_id") Integer descriptionId) {
        return productService.deleteDescription(userId, token, productId, descriptionId);
    }
}
