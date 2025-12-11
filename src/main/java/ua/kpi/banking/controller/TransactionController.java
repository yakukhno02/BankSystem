package ua.kpi.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.banking.model.Transaction;
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
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.makeTransaction(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fromAccount/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByFromAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAllByFromAccountId(accountId));
    }

    @GetMapping("/toAccount/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByToAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getAllByToAccountId(accountId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@RequestParam TransactionType type) {
        return ResponseEntity.ok(transactionService.getByType(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Transaction> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
