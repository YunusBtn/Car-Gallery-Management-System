package com.yunus.controller;

import com.yunus.dto.DtoCustomer;
import com.yunus.dto.DtoCustomerIU;
import com.yunus.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/save")
    public DtoCustomer saveCustomer(@Valid @RequestBody DtoCustomerIU dtoCustomerIU) {
        return customerService.saveCustomer(dtoCustomerIU);
    }

    @GetMapping("/list")
    public List<DtoCustomer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public DtoCustomer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }
}
