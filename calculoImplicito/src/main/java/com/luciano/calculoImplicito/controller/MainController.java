package com.luciano.calculoImplicito.controller;

import com.luciano.calculoImplicito.model.CalcResponse;
import com.luciano.calculoImplicito.model.Simulation;
import com.luciano.calculoImplicito.repository.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
public class MainController {

    @Autowired
    private SimulationRepository simulationRepository;

    @GetMapping("/binomial/{n}/{k}")
    public CalcResponse calcular(@PathVariable int n, @PathVariable int k) {
        if (k < 0 || k > n || n < 0) {
            return new CalcResponse("Erro", "Parâmetros inválidos: k deve estar entre 0 e n.");
        }

        try {
            long coef = CombinatoricsUtils.binomialCoefficient(n, k);
            String resultado = "C(" + n + "," + k + ") = " + coef;
            String explicacao = "Número de maneiras de escolher " + k + " sucessos em " + n + " tentativas. Útil para calcular combinações em paylines de slots.";

            saveSimulation("coeficiente", n, k, null, resultado + " | " + explicacao);
            return new CalcResponse(resultado, explicacao);
        } catch (Exception e) {
            return new CalcResponse("Erro", "Valor muito grande para calcular.");
        }
    }

    // Novo endpoint: probabilidade binomial
    @GetMapping("/binomial/prob/{n}/{k}/{p}")
    public CalcResponse probabilidade(@PathVariable int n, @PathVariable int k, @PathVariable double p) {
        if (k < 0 || k > n || n < 0) {
            return new CalcResponse("Erro", "Parâmetros inválidos.");
        }
        if (p < 0 || p > 1) {
            return new CalcResponse("Erro", "p deve estar entre 0 e 1.");
        }

        try {
            BinomialDistribution dist = new BinomialDistribution(n, p);
            double prob = dist.probability(k);

            String resultado = String.format(
                    "Probabilidade de exatamente %d sucessos em %d tentativas (p=%.3f) = %.6f (%.4f%%)",
                    k, n, p, prob, prob * 100
            );

            String explicacao = "Isso é a chance real de um evento acontecer exatamente " + k + " vezes em " + n + " tentativas. " +
                    "Em cassinos, p geralmente é menor que 0.5 (ex: ~0.486 na roleta europeia por causa do zero). " +
                    "Quanto maior o n, mais a probabilidade se aproxima da média — e a casa sempre tem edge positivo a longo prazo.";

            if (p < 0.5) {
                explicacao += "\n\nDica: Quando p < 0.5, a expectativa é perda gradual. Estratégias como Martingale não mudam isso — só adiam a quebra.";
            }

            String fullResult = resultado + "\n\n" + explicacao;
            saveSimulation("probabilidade", n, k, p, fullResult);

            return new CalcResponse(resultado, explicacao);

        } catch (Exception e) {
            return new CalcResponse("Erro", "Falha no cálculo: " + e.getMessage());
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
            @RequestParam double p,
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
        long count = simulationRepository.count();
        simulationRepository.deleteAll();
        return "Histórico limpo! " + count + " simulações deletadas.";
    }
}