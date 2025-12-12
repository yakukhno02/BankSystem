package ua.kpi.banking.dto.card;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCardRequest {
    private String cardNumber;
    private LocalDate expirationDate;
    private String type;
    private Long accountId;
}
