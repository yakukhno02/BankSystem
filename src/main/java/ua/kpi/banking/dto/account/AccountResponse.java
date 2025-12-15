package ua.kpi.banking.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.kpi.banking.model.AccountType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String iban;
    private BigDecimal balance;
    private String currency;
    private AccountType type;
    private Long customerId;
}
