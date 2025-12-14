package ua.kpi.banking.dto.card;

import lombok.Getter;
import lombok.Setter;
import ua.kpi.banking.model.CardStatus;
import ua.kpi.banking.model.CardType;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCardRequest {
    private String cardNumber;
    private LocalDate expirationDate;
    private CardType type;
    private CardStatus status;
    private Long accountId;
}
