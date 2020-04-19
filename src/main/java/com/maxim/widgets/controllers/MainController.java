package com.maxim.widgets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class MainController {

    @GetMapping("/")
    public String main(Model model) {
        return "index";
    }
}