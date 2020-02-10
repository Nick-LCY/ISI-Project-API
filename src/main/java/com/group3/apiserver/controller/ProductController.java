package com.group3.apiserver.controller;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    public PaginationDTO<ProductEntity> getProductList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(name = "key", defaultValue = "") String key,
                                                       @RequestParam(name = "category", defaultValue = "____") String category,
                                                       @RequestParam(name = "order_by", defaultValue = "1") Integer orderBy) {
        return productService.findProducts(page, key, category, orderBy);
    }

    @GetMapping("/product")
    public ProductEntity getProduct(@RequestParam(name = "id") Integer id) {
        return productService.findProduct(id);
    }
}
