package com.mocha.shopwebsite.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    private static Helper instance;

    public static Helper getInstance() {
        return instance;
    }

    public Helper() {
        instance = this;
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

}
