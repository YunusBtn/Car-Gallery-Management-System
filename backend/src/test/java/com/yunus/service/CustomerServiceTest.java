package com.yunus.service;

import com.yunus.dto.DtoCustomer;
import com.yunus.dto.DtoCustomerIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.CustomerMapper;
import com.yunus.model.Account;
import com.yunus.model.Address;
import com.yunus.model.Customer;
import com.yunus.model.Role;
import com.yunus.model.User;
import com.yunus.repository.AccountRepository;
import com.yunus.repository.AddressRepository;
import com.yunus.repository.CustomerRepository;
import com.yunus.repository.RoleRepository;
import com.yunus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Testleri")
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;
    @Mock private AddressRepository addressRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;

    private static final Long ADDRESS_ID = 10L;
    private static final Long ACCOUNT_ID = 20L;
    private static final String USERNAME  = "testuser";

    private DtoCustomerIU dtoCustomerIU;
    private Address address;
    private Account account;
    private User user;
    private Role customerRole;
    private Customer customer;
    private DtoCustomer dtoCustomer;

    @BeforeEach
    void setUp() {
        dtoCustomerIU = new DtoCustomerIU();
        dtoCustomerIU.setFirstName("Ahmet");
        dtoCustomerIU.setLastName("Yılmaz");
        dtoCustomerIU.setTckn("12345678901");
        dtoCustomerIU.setAddressId(ADDRESS_ID);
        dtoCustomerIU.setAccountId(ACCOUNT_ID);

        address = new Address();
        address.setId(ADDRESS_ID);

        account = new Account();
        account.setId(ACCOUNT_ID);

        customerRole = new Role();
        customerRole.setName(Role.RoleName.CUSTOMER);

        user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setRoles(new HashSet<>());

        customer = new Customer();
        customer.setFirstName("Ahmet");

        dtoCustomer = new DtoCustomer();
        dtoCustomer.setFirstName("Ahmet");
    }

    // ─────────────────────────────────────────────
    // Yardımcı: SecurityContextHolder mock setup
    // ─────────────────────────────────────────────

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mockedSecurity) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication auth = Mockito.mock(Authentication.class);
        mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(USERNAME);
    }

    // ─────────────────────────────────────────────
    // saveCustomer
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveCustomer: Tüm bağımlılıklar mevcutsa müşteri kaydedilmeli")
    void saveCustomer_shouldReturnDtoCustomer_whenAllFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
            when(customerMapper.toEntity(dtoCustomerIU)).thenReturn(customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            when(customerMapper.toDto(customer)).thenReturn(dtoCustomer);

            // When
            DtoCustomer result = customerService.saveCustomer(dtoCustomerIU);

            // Then
            assertNotNull(result);
            assertEquals("Ahmet", result.getFirstName());
            verify(customerRepository, times(1)).save(any(Customer.class));
            verify(userRepository, times(1)).save(user); // role eklendikten sonra user kaydedilmeli
        }
    }

    @Test
    @DisplayName("saveCustomer: Adres bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveCustomer_shouldThrow_whenAddressNotFound() {
        // Given
        when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> customerService.saveCustomer(dtoCustomerIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveCustomer: Hesap bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveCustomer_shouldThrow_whenAccountNotFound() {
        // Given
        when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> customerService.saveCustomer(dtoCustomerIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveCustomer: Kullanıcı bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveCustomer_shouldThrow_whenUserNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            // When & Then
            BaseException ex = assertThrows(BaseException.class,
                    () -> customerService.saveCustomer(dtoCustomerIU));
            assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
            verify(customerRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("saveCustomer: Müşteri rolü bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveCustomer_shouldThrow_whenCustomerRoleNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.empty());

            // When & Then
            BaseException ex = assertThrows(BaseException.class,
                    () -> customerService.saveCustomer(dtoCustomerIU));
            assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
            verify(customerRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // getAllCustomers
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllCustomers: Müşteri varsa DTO listesi dönmeli")
    void getAllCustomers_shouldReturnList() {
        // Given
        Customer customer2 = new Customer();
        DtoCustomer dtoCustomer2 = new DtoCustomer();

        when(customerRepository.findAll()).thenReturn(List.of(customer, customer2));
        when(customerMapper.toDto(customer)).thenReturn(dtoCustomer);
        when(customerMapper.toDto(customer2)).thenReturn(dtoCustomer2);

        // When
        List<DtoCustomer> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllCustomers: Müşteri yoksa boş liste dönmeli")
    void getAllCustomers_shouldReturnEmptyList() {
        // Given
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DtoCustomer> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // getCustomerById
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getCustomerById: Müşteri bulunduğunda DtoCustomer dönmeli")
    void getCustomerById_shouldReturnDtoCustomer_whenFound() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(dtoCustomer);

        // When
        DtoCustomer result = customerService.getCustomerById(1L);

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("getCustomerById: Müşteri bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void getCustomerById_shouldThrow_whenNotFound() {
        // Given
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> customerService.getCustomerById(99L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    // ─────────────────────────────────────────────
    // getMyProfile
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getMyProfile: Giriş yapan kullanıcıya ait müşteri profili dönmeli")
    void getMyProfile_shouldReturnDtoCustomer_whenCustomerExists() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            customer.setUser(user);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(customerRepository.findAll()).thenReturn(List.of(customer));
            when(customerMapper.toDto(customer)).thenReturn(dtoCustomer);

            // When
            DtoCustomer result = customerService.getMyProfile();

            // Then
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("getMyProfile: Müşteri kaydı yoksa null dönmeli")
    void getMyProfile_shouldReturnNull_whenNoCustomerForUser() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(customerRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            DtoCustomer result = customerService.getMyProfile();

            // Then
            assertNull(result);
        }
    }
}
