package com.luciano.calculoImplicito.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "simulations")
public class Simulation {

    @Id
    private String id;

    private String type;          // "coeficiente" ou "probabilidade"
    private int n;
    private int k;
    private Double p;             // só para probabilidade (pode ser null)
    private String result;        // o texto completo do resultado
    private LocalDateTime timestamp = LocalDateTime.now();

    // Construtor vazio (obrigatório)
    public Simulation() {}

    // Getters e Setters (gere no IntelliJ com Alt+Insert ou escreva manualmente)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getN() { return n; }
    public void setN(int n) { this.n = n; }
    public int getK() { return k; }
    public void setK(int k) { this.k = k; }
    public Double getP() { return p; }
    public void setP(Double p) { this.p = p; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}