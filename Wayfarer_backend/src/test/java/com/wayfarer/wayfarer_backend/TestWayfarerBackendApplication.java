package com.wayfarer.wayfarer_backend;

import org.springframework.boot.SpringApplication;

public class TestWayfarerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(WayfarerBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
