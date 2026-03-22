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

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;


    public DtoAddress saveAddress(DtoAddressIU dtoAddressIU) {

        Address Address = addressMapper.toEntity(dtoAddressIU);
        Address savedAddress = addressRepository.save(Address);

        return addressMapper.toDto(savedAddress);
    }


}
