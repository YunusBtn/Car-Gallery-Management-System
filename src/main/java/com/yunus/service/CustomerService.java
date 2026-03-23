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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public DtoCustomer saveCustomer(DtoCustomerIU dtoCustomerIU) {

        Optional<Address> optAddress = addressRepository.findById(dtoCustomerIU.getAddressId());

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

    public List<DtoCustomer> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    public DtoCustomer getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return customerMapper.toDto(customer);
    }
}
