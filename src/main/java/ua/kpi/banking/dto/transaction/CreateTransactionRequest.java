package ua.kpi.banking.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateTransactionRequest {
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime date;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
}
