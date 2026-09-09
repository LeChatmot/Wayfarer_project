package com.wayfarer.wayfarer_backend.dto.itemlist_dto;

import java.util.List;

public record ItemListResponse(Integer id, String name, List<ItemResponse> items) {
}