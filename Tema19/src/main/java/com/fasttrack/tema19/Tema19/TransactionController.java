package com.fasttrack.tema19.Tema19;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private List<Transaction> transactions = new ArrayList<>();

    @GetMapping
    public List<Transaction> getAllTransactions(
            @RequestParam(required = false) String product,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount
    ) {
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t ->
                        (product == null || t.getProduct().equals(product)) &&
                                (type == null || t.getType() == type) &&
                                (minAmount == null || t.getAmount() >= minAmount) &&
                                (maxAmount == null || t.getAmount() <= maxAmount)

                )
                .collect(Collectors.toList());
        return filteredTransactions;
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactions.stream()
                .filter(t -> Objects.equals(t.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        transaction.setId((long) (transactions.size() + 1));
        transactions.add(transaction);
        return transaction;
    }

    @PutMapping("/{id}")
    public Transaction replaceTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(id);
        if(existingTransaction != null) {
            existingTransaction.setProduct(updatedTransaction.getProduct());
            existingTransaction.setType(updatedTransaction.getType());
            existingTransaction.setAmount(updatedTransaction.getAmount());
            return existingTransaction;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactions.removeIf(t -> Objects.equals(t.getId(), id));
    }

    @GetMapping("/reports/type")
    public Map<TransactionType, List<Transaction>> getTypeReports(){
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getType));
    }

    @GetMapping("/reports/product")
    public Map<String, List<Transaction>> getProductReports(){
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getProduct));
    }
}
