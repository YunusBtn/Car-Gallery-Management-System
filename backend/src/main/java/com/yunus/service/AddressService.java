package com.yunus.service;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
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

        Address address = addressMapper.toEntity(dtoAddressIU);
        Address savedAddress = addressRepository.save(address);

        return addressMapper.toDto(savedAddress);
    }

    public DtoAddress getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return addressMapper.toDto(address);
    }

}
