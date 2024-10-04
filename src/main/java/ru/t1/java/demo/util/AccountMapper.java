package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

@Component
public class AccountMapper {
    public static Account toEntity(AccountDto accountDto) {
        return Account.builder()
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance())
                .clientId(accountDto.getClientId())
                .build();
    }

    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .clientId(account.getClientId())
                .build();
    }
}
