package ua.kpi.banking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.kpi.banking.dto.card.CardResponse;
import ua.kpi.banking.dto.card.CreateCardRequest;
import ua.kpi.banking.exception.BadRequestException;
import ua.kpi.banking.exception.NotFoundException;
import ua.kpi.banking.model.*;
import ua.kpi.banking.repository.AccountRepository;
import ua.kpi.banking.repository.CardRepository;
import ua.kpi.banking.repository.CustomerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CardService cardService;

    private Account account;
    private Card activeCard;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(10L);

        activeCard = new Card();
        activeCard.setId(1L);
        activeCard.setCardNumber("1234567890123456");
        activeCard.setExpirationDate(LocalDate.now().plusYears(2));
        activeCard.setType(CardType.DEBIT);
        activeCard.setStatus(CardStatus.ACTIVE);
        activeCard.setAccount(account);
    }

    @Test
    void shouldCreateCardForExistingAccount() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        CreateCardRequest req = new CreateCardRequest();
        req.setCardNumber("1111222233334444");
        req.setExpirationDate(LocalDate.now().plusYears(3));
        req.setType(CardType.CREDIT);
        req.setAccountId(10L);

        CardResponse response = cardService.createCard(req);

        assertEquals("1111222233334444", response.getCardNumber());
        assertEquals(CardType.CREDIT, response.getType());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowWhenCreatingCardForMissingAccount() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        CreateCardRequest req = new CreateCardRequest();
        req.setAccountId(99L);

        assertThrows(NotFoundException.class, () -> cardService.createCard(req));
        verifyNoInteractions(cardRepository);
    }

    @Test
    void shouldGetCardById() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        CardResponse response = cardService.getCardById(1L);

        assertEquals("1234567890123456", response.getCardNumber());
    }

    @Test
    void shouldThrowWhenCardByIdNotFound() {
        when(cardRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getCardById(404L));
    }

    @Test
    void shouldGetCardByNumber() {
        when(cardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(activeCard));

        CardResponse response = cardService.getCardByNumber("1234567890123456");

        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowWhenCardNumberNotFound() {
        when(cardRepository.findByCardNumber("0000000000000000")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getCardByNumber("0000000000000000"));
    }

    @Test
    void shouldGetAllCardsByAccountId() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(cardRepository.findByAccountId(10L)).thenReturn(List.of(activeCard));

        List<CardResponse> result = cardService.getAllCardsByAccountId(10L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenGettingCardsForMissingAccount() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getAllCardsByAccountId(99L));
        verifyNoInteractions(cardRepository);
    }

    @Test
    void shouldGetAllCardsByCustomerId() {
        Customer customer = new Customer();
        customer.setId(5L);

        when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));
        when(cardRepository.findByAccountCustomerId(5L)).thenReturn(List.of(activeCard));

        List<CardResponse> result = cardService.getAllCardsByCustomerId(5L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenGettingCardsForMissingCustomer() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getAllCardsByCustomerId(99L));
        verifyNoInteractions(cardRepository);
    }

    @Test
    void shouldBlockActiveCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardResponse response = cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, response.getStatus());
    }

    @Test
    void shouldThrowWhenBlockingAlreadyBlockedCard() {
        activeCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        assertThrows(BadRequestException.class, () -> cardService.blockCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenBlockingClosedCard() {
        activeCard.setStatus(CardStatus.CLOSED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        assertThrows(BadRequestException.class, () -> cardService.blockCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenBlockingMissingCard() {
        when(cardRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.blockCard(404L));
    }

    @Test
    void shouldCloseActiveCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardResponse response = cardService.closeCard(1L);

        assertEquals(CardStatus.CLOSED, response.getStatus());
    }

    @Test
    void shouldCloseBlockedCard() {
        activeCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardResponse response = cardService.closeCard(1L);

        assertEquals(CardStatus.CLOSED, response.getStatus());
    }

    @Test
    void shouldThrowWhenClosingAlreadyClosedCard() {
        activeCard.setStatus(CardStatus.CLOSED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        assertThrows(BadRequestException.class, () -> cardService.closeCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenClosingMissingCard() {
        when(cardRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.closeCard(404L));
    }
}