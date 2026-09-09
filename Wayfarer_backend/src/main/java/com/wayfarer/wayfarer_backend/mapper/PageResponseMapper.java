package com.wayfarer.wayfarer_backend.mapper;

import com.wayfarer.wayfarer_backend.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <T> PageResponse<T> toDto(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}