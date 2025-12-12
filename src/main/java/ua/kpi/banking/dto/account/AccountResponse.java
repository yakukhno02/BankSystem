package ua.kpi.banking.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String iban;
    private BigDecimal balance;
    private String currency;
    private String type;
    private Long customerId;
}
