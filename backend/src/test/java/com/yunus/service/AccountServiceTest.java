package com.yunus.service;

import com.yunus.dto.DtoAccount;
import com.yunus.dto.DtoAccountIU;
import com.yunus.enums.CurrencyType;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.AccountMapper;
import com.yunus.model.Account;
import com.yunus.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Testleri")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    private DtoAccountIU dtoAccountIU;
    private Account account;
    private DtoAccount dtoAccount;

    @BeforeEach
    void setUp() {
        dtoAccountIU = new DtoAccountIU();
        dtoAccountIU.setAccountNo("ACC001");
        dtoAccountIU.setIban("TR123456789012345678901234");
        dtoAccountIU.setAmount(new BigDecimal("50000"));
        dtoAccountIU.setCurrencyType(CurrencyType.TL);

        account = new Account();
        account.setId(1L);
        account.setAccountNo("ACC001");
        account.setAmount(new BigDecimal("50000"));
        account.setCurrencyType(CurrencyType.TL);

        dtoAccount = new DtoAccount();
        dtoAccount.setId(1L);
        dtoAccount.setAccountNo("ACC001");
        dtoAccount.setAmount(new BigDecimal("50000"));
    }

    // ─────────────────────────────────────────────
    // saveAccount
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveAccount: Geçerli DTO ile hesap kaydedilmeli ve DtoAccount dönmeli")
    void saveAccount_shouldReturnDtoAccount() {
        // Given
        when(accountMapper.toEntity(dtoAccountIU)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(dtoAccount);

        // When
        DtoAccount result = accountService.saveAccount(dtoAccountIU);

        // Then
        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNo());
        assertEquals(0, new BigDecimal("50000").compareTo(result.getAmount()));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("saveAccount: Mapper ve repository doğru sırayla çağrılmalı")
    void saveAccount_shouldCallMapperAndRepositoryInOrder() {
        // Given
        when(accountMapper.toEntity(dtoAccountIU)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(dtoAccount);

        // When
        accountService.saveAccount(dtoAccountIU);

        // Then
        var inOrder = inOrder(accountMapper, accountRepository);
        inOrder.verify(accountMapper).toEntity(dtoAccountIU);
        inOrder.verify(accountRepository).save(account);
        inOrder.verify(accountMapper).toDto(account);
    }

    // ─────────────────────────────────────────────
    // getAccountById
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAccountById: Hesap bulunduğunda DtoAccount dönmeli")
    void getAccountById_shouldReturnDtoAccount_whenFound() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(dtoAccount);

        // When
        DtoAccount result = accountService.getAccountById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACC001", result.getAccountNo());
    }

    @Test
    @DisplayName("getAccountById: Hesap bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void getAccountById_shouldThrow_whenNotFound() {
        // Given
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> accountService.getAccountById(99L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(accountMapper, never()).toDto(any());
    }
}
