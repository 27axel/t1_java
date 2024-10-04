package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final KafkaClientProducer kafkaClientProducer;

    @Value("${t1.kafka.topic.transactions}")
    private String topic;

    @Override
    public void registerAccounts(List<Transaction> transactions) {
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        savedTransactions.forEach(transaction -> {
            kafkaClientProducer.sendTo(topic, transaction);
        });
    }

    @Override
    public List<TransactionDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactions;
        try {
            transactions = mapper.readValue(new File("src/main/resources/TRANSACTION_DATA.json"), TransactionDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(transactions);
    }
}
