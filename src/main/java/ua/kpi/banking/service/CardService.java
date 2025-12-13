package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.dto.card.CardResponse;
import ua.kpi.banking.dto.card.CreateCardRequest;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Card;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CardService(CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    public CardResponse createCard(CreateCardRequest card) {

        Account account = accountRepository.findById(card.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Card newCard = new Card();
        newCard.setCardNumber(card.getCardNumber());
        newCard.setExpirationDate(card.getExpirationDate());
        newCard.setType(card.getType());
        newCard.setAccount(account);

        cardRepository.save(newCard);

        return toResponse(newCard);
    }

    public CardResponse getCardById(Long id) {
        return toResponse(cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found")));
    }

    public CardResponse getCardByNumber(String cardNumber) {
        return toResponse(cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found")));
    }

    public List<CardResponse> getAllCardsByAccountId(Long accountId) {
        return cardRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CardResponse> getAllCardsByCustomerId(Long customerId) {
        return cardRepository.findByAccountCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteCard(Long id) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Card with id " + id + " not found"));
        cardRepository.deleteById(id);
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getCardNumber(),
                card.getExpirationDate(),
                card.getType(),
                card.getAccount().getId()
        );
    }
}
