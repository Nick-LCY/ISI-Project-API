package com.group3.apiserver.service;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.ProductDetailDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.repository.ProductDescriptionRepository;
import com.group3.apiserver.repository.ProductPhotographRepository;
import com.group3.apiserver.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private ProductPhotographRepository productPhotographRepository;
    private ProductDescriptionRepository productDescriptionRepository;

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

    public PaginationDTO<ProductEntity> findProducts(Integer page, String key, String category, Integer orderBy) {
        Pageable pageable = PageRequest.of(page - 1, 8, orderBy>0?Sort.by("price"):Sort.by("price").descending());
        return new PaginationDTO<>(
                productRepository.findAllByNameLikeAndCategoryLike("%" + key + "%", category, pageable),
                page);
    }

    public ProductEntity findProduct(Integer id) {
        Optional<ProductEntity> productEntityOptional =productRepository.findById(id);
        ProductDetailDTO productDetailDTO = null;
        if (productEntityOptional.isPresent()) {
            productDetailDTO = new ProductDetailDTO(productEntityOptional.get());
            productDetailDTO.setProductPhotographs(productPhotographRepository.findAllByProductId(productDetailDTO.getId()));
            productDetailDTO.setProductDescriptions(productDescriptionRepository.findAllByProductId(productDetailDTO.getId()));
        }
        return productDetailDTO;
    }
}
