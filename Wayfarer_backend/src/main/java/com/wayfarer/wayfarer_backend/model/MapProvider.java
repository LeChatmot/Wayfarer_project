package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "map_providers")
public class MapProvider extends BaseEntity {

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "url")
    @NotNull
    private String url;


    @Column(name = "attribution")
    @NotNull
    private String attribution;

    @Column(name = "is_default")
    @NotNull
    private Boolean isDefault;

    @Column(name = "invertible")
    @NotNull
    private Boolean invertible;

    public MapProvider() {
        this.setName("");
        this.setUrl("");
        this.setAttribution("");
        this.setIsDefault(false);
        this.setInvertible(true);
    }
}
