package com.yunus.controller;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/save")
    public DtoAddress saveAddress(@Valid @RequestBody DtoAddressIU dtoAddressIU) {
        return addressService.saveAddress(dtoAddressIU);
    }

    @GetMapping("/{id}")
    public DtoAddress getAddressById(@PathVariable Long id) {
        return addressService.getAddressById(id);
    }
}
