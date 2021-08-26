package com.jmeterpractice.practice.controllers;

import com.jmeterpractice.practice.modules.JmeterTesting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MainController {

    @GetMapping("/")
    public String startPage(Model model) {
        model.addAttribute("title", "Главная страница");
        return "Home";
    }

    @GetMapping("/StartTest")
    public String startTest(Model model) throws Exception {
        model.addAttribute("title", "Главная страница");
        JmeterTesting.getJmeterTest().start();
        return "Home";
    }

    @GetMapping("/StopTest")
    public String stopTest(Model model) {
        model.addAttribute("title", "Главная страница");
        JmeterTesting.getJmeterTest().stop();
        return "Home";
    }

}
