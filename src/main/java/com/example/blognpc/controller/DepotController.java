package com.example.blognpc.controller;

import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DepotController {
    @Autowired
    private ToolService toolService;

    @GetMapping("/depot")
    public String tool(Model model,
                       @RequestParam(name = "page", defaultValue = "1") Long page,
                       @RequestParam(name = "size", defaultValue = "10") Long size,
                       @RequestParam(name = "search", required = false) String search) {
        PaginationDTO paginationDTO = toolService.list(0L, page, size, search, "gmt_create");

        model.addAttribute("search", search);
        model.addAttribute("paginationDTO", paginationDTO);
        return "depot";
    }


}
