package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

@Component
public class TransactionMapper {
    public static Transaction toEntity(TransactionDto transactionDto) {
        return Transaction.builder()
                .amount(transactionDto.getAmount())
                .accountId(transactionDto.getAccountId())
                .clientId(transactionDto.getClientId())
                .build();
    }

    public static TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .amount(transaction.getAmount())
                .accountId(transaction.getAccountId())
                .clientId(transaction.getClientId())
                .build();
    }
}
