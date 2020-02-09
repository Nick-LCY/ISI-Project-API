package com.group3.apiserver.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginationDTO<T> {
    public PaginationDTO(Page<T> page, Integer currentPage) {
        this.productEntityList = page.toList();
        this.totalPages = page.getTotalPages();
        this.currentPage = currentPage;
    }

    List<T> productEntityList;
    Integer totalPages;
    Integer currentPage;
}
