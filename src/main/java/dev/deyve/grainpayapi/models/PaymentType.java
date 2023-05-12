package dev.deyve.grainpayapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {

    @JsonProperty("Money")
    MONEY("Money"),

    @JsonProperty("VR")
    VR("VR"),

    @JsonProperty("Credit Card")
    CREDIT_CARD("Credit Card");

    private String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
