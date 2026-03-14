package com.yunus.service;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.mapper.AddressMapper;
import com.yunus.model.Address;

import com.yunus.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository AddressRepository;
    private final AddressMapper AddressMapper;


    public DtoAddress saveAddress(DtoAddressIU dtoAddressIU) {

        Address Address = AddressMapper.toEntity(dtoAddressIU);
        Address savedAddress = AddressRepository.save(Address);

        return AddressMapper.toDto(savedAddress);
    }


}
