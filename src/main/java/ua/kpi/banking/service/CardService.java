package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.banking.model.Card;
import ua.kpi.banking.repository.CardRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public Optional<Card> getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    public List<Card> getAllCardsByAccountId(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    public List<Card> getAllCardsByCustomerId(Long customerId) {
        return cardRepository.findByAccountCustomerId(customerId);
    }

    public Card updateCard(Long id, Card card) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Card with id " + id + " not found"));

        existingCard.setType(card.getType());
        existingCard.setExpirationDate(card.getExpirationDate());
        return cardRepository.save(existingCard);
    }

    public void deleteCard(Long id) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Card with id " + id + " not found"));
        cardRepository.deleteById(id);
    }
}
