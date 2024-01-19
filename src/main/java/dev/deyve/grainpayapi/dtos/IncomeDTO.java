package dev.deyve.grainpayapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncomeDTO {

    private Long id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public IncomeDTO() {
    }

    public IncomeDTO(Long id, String description, BigDecimal amount, LocalDate date, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomeDTO incomeDTO = (IncomeDTO) o;
        return Objects.equals(id, incomeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
