package com.yunus.controller;

import com.yunus.dto.DtoCustomer;
import com.yunus.dto.DtoCustomerIU;
import com.yunus.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/save")
    public DtoCustomer saveCustomer(@Valid @RequestBody DtoCustomerIU dtoCustomerIU) {
        return customerService.saveCustomer(dtoCustomerIU);
    }


}
