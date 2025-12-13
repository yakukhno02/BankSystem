package ua.kpi.banking.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.kpi.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime date;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
}
