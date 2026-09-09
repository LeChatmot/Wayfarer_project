package com.wayfarer.wayfarer_backend.service.itemList_service;

import com.wayfarer.wayfarer_backend.dto.itemlist_dto.*;
import com.wayfarer.wayfarer_backend.model.Item;
import com.wayfarer.wayfarer_backend.model.ItemList;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.ItemListRepository;
import com.wayfarer.wayfarer_backend.service.auth_service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemListService {

    private final ItemListRepository itemListRepository;
    private final CurrentUserService currentUserService;

    public ItemListService(ItemListRepository itemListRepository, CurrentUserService currentUserService) {
        this.itemListRepository = itemListRepository;
        this.currentUserService = currentUserService;
    }

    public List<ItemListResponse> getCurrentUserLists() {
        User user = currentUserService.getCurrentUser();
        return itemListRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ItemListResponse createList(ItemListCreateRequest request) {
        User user = currentUserService.getCurrentUser();
        ItemList list = new ItemList();
        list.setUser(user);
        list.setName(request.name());
        return toResponse(itemListRepository.save(list));
    }

    @Transactional
    public ItemListResponse addItem(Integer listId, ItemCreateRequest request) {
        ItemList list = getOwnedList(listId);
        Item item = new Item(list, request.name(), request.quantity());
        list.getItems().add(item);
        return toResponse(itemListRepository.save(list));
    }

    @Transactional
    public void removeItem(Integer listId, Integer itemId) {
        ItemList list = getOwnedList(listId);
        boolean removed = list.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new IllegalArgumentException("Objet introuvable dans cette liste");
        }
    }

    @Transactional
    public ItemListResponse updateItem(Integer listId, Integer itemId, ItemUpdateRequest request) {
        ItemList list = getOwnedList(listId);
        Item item = list.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Objet introuvable dans cette liste"));
        item.setName(request.name());
        item.setQuantity(request.quantity());
        return toResponse(itemListRepository.save(list));
    }

    @Transactional
    public void deleteList(Integer listId) {
        ItemList list = getOwnedList(listId);
        itemListRepository.delete(list);
    }

    private ItemList getOwnedList(Integer listId) {
        User user = currentUserService.getCurrentUser();
        return itemListRepository.findByIdAndUserId(listId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Liste introuvable"));
    }

    private ItemListResponse toResponse(ItemList list) {
        List<ItemResponse> items = list.getItems().stream()
                .map(item -> new ItemResponse(item.getId(), item.getName(), item.getQuantity()))
                .toList();
        return new ItemListResponse(list.getId(), list.getName(), items);
    }
}