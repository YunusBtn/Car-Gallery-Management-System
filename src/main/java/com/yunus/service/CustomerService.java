package com.yunus.service;


import com.yunus.dto.DtoCustomer;
import com.yunus.dto.DtoCustomerIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.CustomerMapper;
import com.yunus.model.Account;
import com.yunus.model.Address;
import com.yunus.model.Customer;
import com.yunus.repository.AccountRepository;
import com.yunus.repository.AddressRepository;
import com.yunus.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressRepository AddressRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public DtoCustomer saveCustomer(DtoCustomerIU dtoCustomerIU) {

        Optional<Address> optAddress = AddressRepository.findById(dtoCustomerIU.getAddressId());

        if (optAddress.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Girilen Adress id Geçersiz");
        }

        Optional<Account> optAccount = accountRepository.findById(dtoCustomerIU.getAccountId());
        if (optAccount.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Girilen Account id Geçersiz");
        }

        Customer customer = customerMapper.toEntity(dtoCustomerIU);
        customer.setAddress(optAddress.get());
        customer.setAccount(optAccount.get());


        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toDto(savedCustomer);

    }


}
