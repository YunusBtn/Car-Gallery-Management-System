package com.yunus.service;

import com.yunus.dto.DtoAccount;
import com.yunus.dto.DtoAccountIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.AccountMapper;
import com.yunus.model.Account;
import com.yunus.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;


    @Transactional
    public DtoAccount saveAccount(DtoAccountIU dtoAccountIU){

        Account account = accountMapper.toEntity(dtoAccountIU);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    public DtoAccount getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return accountMapper.toDto(account);
    }

}
