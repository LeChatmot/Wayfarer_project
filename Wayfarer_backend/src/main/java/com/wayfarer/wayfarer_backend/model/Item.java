package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_list_id", nullable = false)
    private ItemList itemList;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "quantity")
    private Integer quantity;

    public Item() {
    }

    public Item(ItemList itemList, String name, Integer quantity) {
        this.itemList = itemList;
        this.name = name;
        this.quantity = quantity;
    }
}