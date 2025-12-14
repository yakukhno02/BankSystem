package ua.kpi.banking.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.kpi.banking.model.AccountType;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class CreateAccountRequest {

    @NotBlank
    @Pattern(
            regexp = "UA[0-9]{10,30}",
            message = "Invalid IBAN format"
    )
    private String iban;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotBlank
    private String currency;

    @NotNull
    private AccountType type;

    @NotNull
    private Long customerId;
}
