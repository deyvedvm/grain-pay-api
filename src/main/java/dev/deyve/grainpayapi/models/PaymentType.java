package dev.deyve.grainpayapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {

    @JsonProperty("Money")
    MONEY("Money"),

    @JsonProperty("PIX")
    PIX("PIX"),

    @JsonProperty("Credit Card")
    CREDIT_CARD("Credit Card"),

    @JsonProperty("Debit Card")
    DEBIT_CARD("Debit Card"),

    @JsonProperty("VR")
    VR("VR"),

    @JsonProperty("VA")
    VA("VA"),

    @JsonProperty("Bank Transfer")
    BANK_TRANSFER("Bank Transfer"),

    @JsonProperty("Boleto")
    BOLETO("Boleto");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
