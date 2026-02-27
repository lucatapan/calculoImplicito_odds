package com.luciano.calculoImplicito.controller;

import com.luciano.calculoImplicito.model.Simulation;
import com.luciano.calculoImplicito.repository.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin("*")
@RestController
public class BinomialController {

    @Autowired
    private SimulationRepository simulationRepository;

    @GetMapping("/binomial/{n}/{k}")
    public String calcular(@PathVariable int n, @PathVariable int k) {
        if (k < 0 || k > n || n < 0) {
            return "Erro: k deve estar entre 0 e n, e n >= 0";
        }

        try {
            long coef = CombinatoricsUtils.binomialCoefficient(n, k);

            // Construa a string uma única vez
            String resultado = "Coeficiente binomial C(" + n + "," + k + ") = " + coef;

            // Salve no MongoDB
            saveSimulation("coeficiente", n, k, null, resultado);

            // Retorne direto
            return resultado;

        } catch (ArithmeticException e) {
            return "Erro: valor muito grande (n muito alto para calcular com precisão)";
        }
    }
    // Novo endpoint: probabilidade binomial
    @GetMapping("/binomial/prob/{n}/{k}/{p}")
    public String probabilidade(@PathVariable int n, @PathVariable int k, @PathVariable double p) {
        if (k < 0 || k > n || n < 0) {
            return "Erro: k deve estar entre 0 e n, n >= 0";
        }
        if (p < 0 || p > 1) {
            return "Erro: p deve estar entre 0 e 1";
        }

        try {
            BinomialDistribution dist = new BinomialDistribution(n, p);
            double prob = dist.probability(k);

            // Construa a string uma única vez
            String resultado = String.format(
                    "Probabilidade de exatamente %d sucessos em %d tentativas (p=%.3f) = %.6f (%.4f%%)",
                    k, n, p, prob, prob * 100
            );

            // Salve no MongoDB
            saveSimulation("probabilidade", n, k, p, resultado);

            // Retorne direto
            return resultado;

        } catch (Exception e) {
            return "Erro ao calcular: " + e.getMessage();
        }
    }
// Novo: Salvar automaticamente após cada cálculo
private void saveSimulation(String type, int n, int k, Double p, String result) {
    Simulation sim = new Simulation();
    sim.setType(type);
    sim.setN(n);
    sim.setK(k);
    sim.setP(p);
    sim.setResult(result);
    simulationRepository.save(sim);
}

// Novo endpoint: Listar todas simulações salvas
@GetMapping("/simulations")
public List<Simulation> getAllSimulations() {
    return simulationRepository.findAll();

        }
    }