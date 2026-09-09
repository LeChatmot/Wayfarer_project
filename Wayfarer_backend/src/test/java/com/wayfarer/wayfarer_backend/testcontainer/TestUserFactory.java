package com.wayfarer.wayfarer_backend.testcontainer;

import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;

public class TestUserFactory {

    public static User build(String email, String userName) {

        User user = new User();
        user.setEmail(email);
        user.setEmailHash("$2a$10$dummyEncodedEmailHash"+userName);
        user.setUsername(userName);
        user.setPassword("$2a$10$dummyEncodedPasswordHash");
        user.setRole(Role.ROLE_USER);

        return user;
    }
}