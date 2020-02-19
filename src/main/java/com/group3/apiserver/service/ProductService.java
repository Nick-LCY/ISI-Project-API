package com.group3.apiserver.service;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.ProductDetailDTO;
import com.group3.apiserver.dto.ProductListItemDTO;
import com.group3.apiserver.entity.ProductEntity;
import com.group3.apiserver.repository.ProductDescriptionRepository;
import com.group3.apiserver.repository.ProductPhotographRepository;
import com.group3.apiserver.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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

    public PaginationDTO<ProductListItemDTO> findProducts(Integer page, String key, String category, Integer orderBy) {
        Pageable pageable = PageRequest.of(page - 1, 8, orderBy>0?Sort.by("price"):Sort.by("price").descending());
        Page<ProductEntity> productPage = productRepository.findAllByNameLikeAndCategoryLike("%" + key + "%", category, pageable);

        PaginationDTO<ProductListItemDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setCurrentPage(page);
        paginationDTO.setTotalPages(productPage.getTotalPages());
        paginationDTO.setItemList(new LinkedList<>());
        for (ProductEntity product :
                productPage.toList()) {
            ProductListItemDTO productListItemDTO = new ProductListItemDTO();
            productListItemDTO.setId(product.getId());
            productListItemDTO.setName(product.getName());
            productListItemDTO.setCategory(product.getCategory());
            productListItemDTO.setPrice(product.getPrice());
            productListItemDTO.setOutOfStock(product.getOutOfStock());
            productListItemDTO.setThumbnailLocation(product.getThumbnailLocation());
            paginationDTO.getItemList().add(productListItemDTO);
        }

        return paginationDTO;
    }

    public ProductDetailDTO findProduct(Integer id) {
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
