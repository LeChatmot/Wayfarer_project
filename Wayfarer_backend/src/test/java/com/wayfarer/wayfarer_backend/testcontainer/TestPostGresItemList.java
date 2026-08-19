package com.wayfarer.wayfarer_backend.testcontainer;

import com.wayfarer.wayfarer_backend.WayfarerBackendApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = WayfarerBackendApplication.class)
@Testcontainers
public class TestPostGresItemList {

}
