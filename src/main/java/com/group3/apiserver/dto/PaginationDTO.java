package com.group3.apiserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaginationDTO<T> {
    public PaginationDTO(Page<T> page, Integer currentPage) {
        this.itemList = page.toList();
        this.totalPages = page.getTotalPages();
        this.currentPage = currentPage;
    }

    List<T> itemList;
    Integer totalPages;
    Integer currentPage;
}
