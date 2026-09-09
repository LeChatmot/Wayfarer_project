package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.itemlist_dto.ItemCreateRequest;
import com.wayfarer.wayfarer_backend.dto.itemlist_dto.ItemListCreateRequest;
import com.wayfarer.wayfarer_backend.dto.itemlist_dto.ItemListResponse;
import com.wayfarer.wayfarer_backend.dto.itemlist_dto.ItemUpdateRequest;
import com.wayfarer.wayfarer_backend.service.itemList_service.ItemListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-lists")
public class ItemListController {

    private final ItemListService itemListService;

    public ItemListController(ItemListService itemListService) {
        this.itemListService = itemListService;
    }

    @GetMapping
    public List<ItemListResponse> getMyLists() {
        return itemListService.getCurrentUserLists();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemListResponse createList(@Valid @RequestBody ItemListCreateRequest request) {
        return itemListService.createList(request);
    }

    @PostMapping("/{listId}/items")
    public ItemListResponse addItem(@PathVariable Integer listId, @Valid @RequestBody ItemCreateRequest request) {
        return itemListService.addItem(listId, request);
    }

    @DeleteMapping("/{listId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Integer listId, @PathVariable Integer itemId) {
        itemListService.removeItem(listId, itemId);
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ItemListResponse updateItem(@PathVariable Integer listId,
                                       @PathVariable Integer itemId,
                                       @Valid @RequestBody ItemUpdateRequest request) {
        return itemListService.updateItem(listId, itemId, request);
    }

    @DeleteMapping("/{listId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteList(@PathVariable Integer listId) {
        itemListService.deleteList(listId);
    }
}