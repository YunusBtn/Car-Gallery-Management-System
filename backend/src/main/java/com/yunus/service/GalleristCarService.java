package com.yunus.service;

import com.yunus.dto.DtoGalleristCar;
import com.yunus.dto.DtoGalleristCarIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.GalleristCarMapper;
import com.yunus.model.Car;
import com.yunus.model.Gallerist;
import com.yunus.model.GalleristCar;
import com.yunus.repository.CarRepository;
import com.yunus.repository.GalleristCarRepository;
import com.yunus.repository.GalleristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleristCarService {

    private final GalleristCarRepository galleristCarRepository;
    private final GalleristCarMapper galleristCarMapper;
    private final CarRepository carRepository;
    private final GalleristRepository galleristRepository;

    @Transactional
    public DtoGalleristCar saveGalleristCar(DtoGalleristCarIU dtoGalleristCarIU) {
        Optional<Gallerist> optGallerist = galleristRepository.findById(dtoGalleristCarIU.getGalleristId());
        if (optGallerist.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Galerici Bulunamadı");
        }
        Optional<Car> optionalCar = carRepository.findById(dtoGalleristCarIU.getCarId());
        if (optionalCar.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Araba bulunamadı" + dtoGalleristCarIU.getCarId());

        }
        GalleristCar galleristCar = galleristCarMapper.toEntity(dtoGalleristCarIU);
        galleristCar.setCar(optionalCar.get());
        galleristCar.setGallerist(optGallerist.get());

        GalleristCar savedGalleristCar = galleristCarRepository.save(galleristCar);

        return galleristCarMapper.toDto(savedGalleristCar);

    }

    public List<DtoGalleristCar> getAllGalleristCars() {
        return galleristCarRepository.findAll()
                .stream()
                .map(galleristCarMapper::toDto)
                .collect(Collectors.toList());
    }

}
