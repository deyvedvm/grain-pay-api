package dev.deyve.grainpayapi.dtos;

import dev.deyve.grainpayapi.models.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseDTO {

    private Long id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;
}
