package ua.kpi.banking.dto.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.kpi.banking.model.AccountType;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class CreateAccountRequest {
    private String iban;
    private BigDecimal balance;
    private String currency;
    private AccountType type;
    private Long customerId;
}
