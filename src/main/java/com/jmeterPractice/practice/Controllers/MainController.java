package com.jmeterPractice.practice.Controllers;

import com.jmeterPractice.practice.HelpClasses.JmeterTesting;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String toDo(Model model) throws Exception {
        model.addAttribute("title", "Главная страница");
        JmeterTesting test = new JmeterTesting();
        test.start();
        return "Greetings from Spring Boot!";
    }
}
