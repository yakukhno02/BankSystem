package ua.kpi.banking.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.kpi.banking.model.CardType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String cardNumber;
    private LocalDate expirationDate;
    private CardType type;
    private Long accountId;
}
