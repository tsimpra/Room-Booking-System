package com.acme.rbs.dto.response.pagination;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class PageDTO<T> {
    private Collection<T> content;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}