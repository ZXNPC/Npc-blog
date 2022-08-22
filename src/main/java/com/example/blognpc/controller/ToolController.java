package com.example.blognpc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ToolController {
    @GetMapping("/tool")
    public String tool() {
        return "tool";
    }
}
