package com.domain.service;

import com.domain.models.User;

import java.util.Set;

public class AdminService {
    private static final Set<String> ADMIN_USERNAMES = Set.of(
            "otricala2718"
    );


    public static boolean isStaticAdmin(String username) {
        return username != null && ADMIN_USERNAMES.contains(username.toLowerCase());
    }
}