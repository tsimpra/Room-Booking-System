package com.acme.rbs.utils;

import com.acme.rbs.dto.response.pagination.PageDTO;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@UtilityClass
public final class PageUtils {

    public static <T, R> PageDTO<T> pageDTOFactory(Page<R> page, Function<Page<R>, List<T>> contentMapping) {
        return PageDTO.<T>builder()
                .content(contentMapping.apply(page))
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
