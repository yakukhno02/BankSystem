package ua.kpi.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.dto.card.CardResponse;
import ua.kpi.banking.dto.card.CreateCardRequest;
import ua.kpi.banking.service.CardService;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public CardResponse createCard(@RequestBody CreateCardRequest card) {
        return cardService.createCard(card);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@RequestParam Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping("/cardNumber/{cardNumber}")
    public ResponseEntity<CardResponse> getCardByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getCardByNumber(cardNumber));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponse>> getAllCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.getAllCardsByAccountId(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CardResponse>> getAllCardsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(cardService.getAllCardsByCustomerId(customerId));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<CardResponse> closeCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.closeCard(id));
    }
}
