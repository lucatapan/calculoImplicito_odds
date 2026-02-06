package com.luciano.calculoImplicito.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BinomialController {

    @GetMapping("/binomial/{n}/{k}")
    public String calcular(@PathVariable int n, @PathVariable int k) {
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