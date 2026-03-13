package com.yunus.service;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.mapper.AdressMapper;
import com.yunus.model.Address;

import com.yunus.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdressService {

    private final AddressRepository adressRepository;
    private final AdressMapper adressMapper;


    public DtoAddress saveAdress(DtoAddressIU dtoAddressIU) {

        Address adress = adressMapper.toEntity(dtoAddressIU);
        Address savedAdress = adressRepository.save(adress);

        return adressMapper.toDto(savedAdress);
    }


}
