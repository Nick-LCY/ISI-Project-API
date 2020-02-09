package com.group3.apiserver.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PaginationDTO<T> {
    public PaginationDTO(Page<T> page, Integer currentPage) {
        this.productEntityList = page.toList();
        this.totalPages = page.getTotalPages();
        this.currentPage = currentPage;
    }

    List<T> productEntityList;
    Integer totalPages;
    Integer currentPage;

    public List<T> getProductEntityList() {
        return productEntityList;
    }

    public void setProductEntityList(List<T> productEntityList) {
        this.productEntityList = productEntityList;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
