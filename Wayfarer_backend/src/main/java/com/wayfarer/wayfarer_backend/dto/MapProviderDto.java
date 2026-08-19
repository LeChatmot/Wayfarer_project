package com.wayfarer.wayfarer_backend.dto;

import lombok.Getter;
import lombok.Setter;

public class MapProviderDto {

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private String attribution;
    @Setter
    @Getter
    private Boolean isDefault;
    @Setter
    @Getter
    private Boolean invertible;
}
