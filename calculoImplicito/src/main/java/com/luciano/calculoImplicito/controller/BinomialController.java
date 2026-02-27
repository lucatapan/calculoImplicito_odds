package com.luciano.calculoImplicito.controller;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BinomialController {

    @GetMapping("/binomial/{n}/{k}")
    public String calcular(@PathVariable int n, @PathVariable int k) {
        if (k < 0 || k > n || n < 0) {
            return "Erro: parâmetros inválidos (k deve estar entre 0 e n, n >= 0)";
        }
        try {
            long coef = CombinatoricsUtils.binomialCoefficient(n, k);
            return "Coeficiente binomial C(" + n + "," + k + ") = " + coef;
        } catch (ArithmeticException e) {
            return "Erro: valor muito grande para calcular com precisão (n muito alto)";
        }
    }
}