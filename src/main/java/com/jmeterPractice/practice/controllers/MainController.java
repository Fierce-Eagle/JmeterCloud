package com.jmeterPractice.practice.controllers;

import com.jmeterPractice.practice.model.JmeterTesting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * Нужен исключительно для запуска и отладки приложения,
     * использовался до по появления кнопок, сейчас можно удалить
     * @param model
     *      обязательный параметр для работы приложения
     * @return
     *      текст после прохождения теста
     */
    @GetMapping("/")
    public String test(Model model) {
        model.addAttribute("title", "Главная страница");
        JmeterTesting.getJmeterTest().start(); // запуск приложения
        return "Test complete!";
    }
}
