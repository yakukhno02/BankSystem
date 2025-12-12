package ua.kpi.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.dto.transaction.CreateTransactionRequest;
import ua.kpi.banking.dto.transaction.TransactionResponse;
import ua.kpi.banking.model.TransactionType;
import ua.kpi.banking.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/fromAccount/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByFromAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAllByFromAccountId(accountId));
    }

    @GetMapping("/toAccount/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByToAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAllByToAccountId(accountId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(@RequestParam TransactionType type) {
        return ResponseEntity.ok(transactionService.getByType(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
