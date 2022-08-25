package com.example.blognpc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManageController {
    @GetMapping("/manage")
    public String manage() {
        return "manage";
    }
}
