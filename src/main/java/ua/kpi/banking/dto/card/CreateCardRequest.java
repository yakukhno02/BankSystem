package ua.kpi.banking.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.CardType;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCardRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must contain 16 digits")
    private String cardNumber;

    @NotNull
    @Future
    private LocalDate expirationDate;

    @NotNull
    private CardType type;

    @NotNull
    private Long accountId;
}
