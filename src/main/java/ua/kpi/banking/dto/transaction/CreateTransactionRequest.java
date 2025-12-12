package ua.kpi.banking.dto.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionRequest {
    private String type;
    private BigDecimal amount;
    private String currency;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
}
