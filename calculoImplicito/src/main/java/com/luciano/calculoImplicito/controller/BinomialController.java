package com.luciano.calculoImplicito.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BinomialController {

    @GetMapping("/binomial")
    public String calcular(@RequestParam int n, @RequestParam int k) {
        return "Coeficiente binomial C(" + n + "," + k + ") = " + binomial(n, k);
    }

    private long binomial(int n, int k) {
        return fatorial(n) / (fatorial(k) * fatorial(n - k));
    }

    private long fatorial(int num) {
        long resultado = 1;
        for (int i = 2; i <= num; i++) {
            resultado *= i;
        }
        return resultado;
    }
}