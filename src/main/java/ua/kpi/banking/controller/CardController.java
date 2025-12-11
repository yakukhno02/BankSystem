package ua.kpi.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.model.Card;
import ua.kpi.banking.service.CardService;

import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardService.createCard(card);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@RequestParam Long id) {
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cardNumber/{cardNumber}")
    public ResponseEntity<Card> getCardByCardNumber(@PathVariable String cardNumber) {
        return cardService.getCardByNumber(cardNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Card>> getAllCardsByAccountId(@RequestParam Long accountId) {
        return ResponseEntity.ok(cardService.getAllCardsByAccountId(accountId));
    }

    @GetMapping("customer/{customerId}")
    public ResponseEntity<List<Card>> getAllCardsByCustomerId(@RequestParam Long customerId) {
        return ResponseEntity.ok(cardService.getAllCardsByCustomerId(customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card card) {
        cardService.updateCard(id, card);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
