package com.kfblue.seh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Smart Energy Hub!";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is running!";
    }
}