package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final KafkaClientProducer kafkaClientProducer;

    @Value("${t1.kafka.topic.accounts}")
    private String topic;

    @Override
    public void registerAccounts(List<Account> accounts) {
        List<Account> savedAccounts = accountRepository.saveAll(accounts);
        savedAccounts.forEach(account -> {
            kafkaClientProducer.sendTo(topic, account);
        });
    }

    @Override
    public List<AccountDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts;
        try {
            accounts = mapper.readValue(new File("src/main/resources/ACCOUNT_DATA.json"), AccountDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(accounts);
    }
}
