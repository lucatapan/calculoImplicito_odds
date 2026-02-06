package com.luciano.calculoImplicito.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String barra() {
        return "A pagina em springboot está funcionando ok rsrs 🚀";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Olá Luciano, precisa arruamar um emprego 🚀";
    }
}
