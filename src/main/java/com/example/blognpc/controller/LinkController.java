package com.example.blognpc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LinkController {
    @GetMapping("/link")
    public String link(Model model,
                       @RequestParam(name = "page", defaultValue = "1") Long page,
                       @RequestParam(name = "size", defaultValue = "10") Long size,
                       @RequestParam(name = "search", required = false) String search)
    {
        model.addAttribute("search", search);
        return "link";
    }
}
