package ua.kpi.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.banking.dto.card.CardResponse;
import ua.kpi.banking.dto.card.CreateCardRequest;
import ua.kpi.banking.exception.BadRequestException;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.Account;
import ua.kpi.banking.model.Card;
import ua.kpi.banking.model.CardStatus;
import ua.kpi.banking.model.Customer;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CardService(CardRepository cardRepository, AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CardResponse createCard(CreateCardRequest card) {

        Account account = accountRepository.findById(card.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

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
                .orElseThrow(() -> new NotFoundException("Card not found")));
    }

    public CardResponse getCardByNumber(String cardNumber) {
        return toResponse(cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Card not found")));
    }

    public List<CardResponse> getAllCardsByAccountId(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        return cardRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CardResponse> getAllCardsByCustomerId(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return cardRepository.findByAccountCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CardResponse blockCard(Long id) {
        Card card =  cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        if (card.getStatus() == CardStatus.CLOSED) {
            throw new BadRequestException("Closed card can't be blocked");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Blocked card can't be blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
        return toResponse(cardRepository.save(card));
    }

    @Transactional
    public CardResponse closeCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.CLOSED) {
            throw new BadRequestException("Card is already closed");
        }

        card.setStatus(CardStatus.CLOSED);
        return toResponse(cardRepository.save(card));
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getCardNumber(),
                card.getExpirationDate(),
                card.getType(),
                card.getStatus(),
                card.getAccount().getId()
        );
    }
}
