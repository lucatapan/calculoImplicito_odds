package com.luciano.calculoImplicito.model;

public class CalcResponse {
    private String result;
    private String explanation;

    public CalcResponse(String result, String explanation) {
        this.result = result;
        this.explanation = explanation;
    }

    // Getters
    public String getResult() { return result; }
    public String getExplanation() { return explanation; }
}