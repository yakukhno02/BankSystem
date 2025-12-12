package ua.kpi.banking.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String cardNumber;
    private LocalDate expirationDate;
    private String type;
    private Long accountId;
}
