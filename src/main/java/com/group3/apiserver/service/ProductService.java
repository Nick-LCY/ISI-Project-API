package com.group3.apiserver.service;

import com.group3.apiserver.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;


    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List findProducts(Integer page, String key, String category, Integer orderBy) {
        Pageable pageable = orderBy>0?
                PageRequest.of(page, 8, Sort.by("price")):
                PageRequest.of(page, 8, Sort.by("price").descending());
        return productRepository.findAllByNameLikeAndCategoryLike("%"+key+"%", category, pageable).toList();
    }
}
