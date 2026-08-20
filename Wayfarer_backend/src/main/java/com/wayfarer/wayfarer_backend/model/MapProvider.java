package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "map_providers")
public class MapProvider extends BaseEntity {

    @Setter
    @Getter
    @Column(name = "name")
    @NotNull
    private String name;

    @Setter
    @Getter
    @Column(name = "url")
    @NotNull
    private String url;

    @Setter
    @Getter
    @Column(name = "attribution")
    @NotNull
    private String attribution;

    @Setter
    @Getter
    @Column(name = "is_default")
    @NotNull
    private Boolean isDefault;

    @Setter
    @Getter
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
