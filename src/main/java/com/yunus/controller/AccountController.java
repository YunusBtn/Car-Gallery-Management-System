package com.yunus.controller;

import com.yunus.dto.DtoAccount;
import com.yunus.dto.DtoAccountIU;
import com.yunus.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/save")
    public DtoAccount saveAccount(@Valid @RequestBody DtoAccountIU dtoAccountIU) {
        return accountService.saveAccount(dtoAccountIU);

    }
}
