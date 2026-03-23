package com.yunus.service;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.GalleristMapper;
import com.yunus.model.Address;
import com.yunus.model.Gallerist;
import com.yunus.repository.AddressRepository;
import com.yunus.repository.GalleristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleristService {

    private final GalleristRepository galleristRepository;
    private final GalleristMapper galleristMapper;
    private final AddressRepository addressRepository;

    @Transactional
    public DtoGallerist saveGallerist(DtoGalleristIU dtoGalleristIU) {

        Optional<Address> optionalAddress = addressRepository.findById(dtoGalleristIU.getAddressId());
        if (optionalAddress.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Adress Bulunamadı");
        }

        Gallerist gallerist = galleristMapper.toEntity(dtoGalleristIU);
        gallerist.setAddress(optionalAddress.get());
        Gallerist savedGallerist = galleristRepository.save(gallerist);

        Gallerist fullGallerist = galleristRepository.findById(savedGallerist.getId()).orElseThrow();

        return galleristMapper.toDto(fullGallerist);
    }

    public List<DtoGallerist> getAllGallerists() {
        return galleristRepository.findAll()
                .stream()
                .map(galleristMapper::toDto)
                .collect(Collectors.toList());
    }

    public DtoGallerist getGalleristById(Long id) {
        Gallerist gallerist = galleristRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return galleristMapper.toDto(gallerist);
    }
}
