package ua.kpi.banking.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime date;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
}
