package com.luciano.calculoImplicito.controller;

import com.luciano.calculoImplicito.model.Simulation;
import com.luciano.calculoImplicito.repository.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.web.bind.annotation.*;


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
    // Novo endpoint: Simulação Martingale
    @GetMapping("/martingale/simulate")
    public String simulateMartingale(
            @RequestParam double bankrollInicial,
            @RequestParam double apostaInicial,
            @RequestParam double p,           // prob de vitória (0 a 1)
            @RequestParam int maxRodadas) {

        if (bankrollInicial <= 0 || apostaInicial <= 0 || p <= 0 || p >= 1 || maxRodadas <= 0) {
            return "Erro: parâmetros inválidos (valores positivos, p entre 0 e 1)";
        }

        double bankroll = bankrollInicial;
        double apostaAtual = apostaInicial;
        int rodadas = 0;
        int vitorias = 0;
        int perdasSeguidas = 0;
        double maxPerda = 0;
        StringBuilder log = new StringBuilder("Simulação Martingale:\n");

        while (rodadas < maxRodadas && bankroll > 0) {
            rodadas++;
            boolean ganhou = Math.random() < p;  // simula rodada (random < p = vitória)

            if (ganhou) {
                bankroll += apostaAtual;
                vitorias++;
                apostaAtual = apostaInicial;  // reset aposta
                perdasSeguidas = 0;
                log.append("Rodada ").append(rodadas).append(": Vitória! Bankroll: ").append(String.format("%.2f", bankroll)).append("\n");
            } else {
                bankroll -= apostaAtual;
                perdasSeguidas++;
                apostaAtual *= 2;  // dobra
                double perdaAtual = Math.max(0, bankrollInicial - bankroll);
                maxPerda = Math.max(maxPerda, perdaAtual);
                log.append("Rodada ").append(rodadas).append(": Perda. Bankroll: ").append(String.format("%.2f", bankroll)).append(" (dobrando para ").append(apostaAtual).append(")\n");
            }

            if (bankroll <= 0) {
                log.append("Quebra total na rodada ").append(rodadas).append("!\n");
                break;
            }
        }

        String resultado = log.toString() + "\n" +
                "Rodadas jogadas: " + rodadas + "\n" +
                "Vitórias: " + vitorias + " (" + String.format("%.2f", (vitorias * 100.0 / rodadas)) + "%)\n" +
                "Bankroll final: " + String.format("%.2f", bankroll) + "\n" +
                "Máxima perda: " + String.format("%.2f", maxPerda) + "\n" +
                (bankroll <= 0 ? "Resultado: QUEBRA TOTAL" : "Resultado: Sobreviveu");

        // Salvar no MongoDB (opcional, mas bom)
        saveSimulation("martingale", maxRodadas, vitorias, p, resultado);

        return resultado;
    }
    // Novo: Limpar todo o histórico
    @DeleteMapping("/simulations/clear")
    public String clearSimulations() {
        long count = simulationRepository.count();  // quantas tem antes
        simulationRepository.deleteAll();           // deleta tudo
        return "Histórico limpo! " + count + " simulações deletadas.";
    }
    }