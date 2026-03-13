package com.yunus.service;

import com.yunus.dto.DtoAccount;
import com.yunus.dto.DtoAccountIU;
import com.yunus.mapper.AccountMapper;
import com.yunus.model.Account;
import com.yunus.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public DtoAccount saveAccount(DtoAccountIU dtoAccountIU){

        Account account = accountMapper.toEntity(dtoAccountIU);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }




}
