package com.yunus.controller;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.service.AdressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adress")
@RequiredArgsConstructor
public class AdressController {

    private final AdressService adressService;

    @PostMapping("/save")
    public DtoAddress saveAdress(@Valid @RequestBody DtoAddressIU dtoAddressIU) {
        return adressService.saveAdress(dtoAddressIU);

    }


}
