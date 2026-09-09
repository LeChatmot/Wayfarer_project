package com.wayfarer.wayfarer_backend.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
}