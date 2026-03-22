package com.yunus.service;

import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.mapper.CarMapper;
import com.yunus.model.Car;
import com.yunus.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public DtoCar saveCar(DtoCarIU dtoCarIU) {
        Car car = carMapper.toEntity(dtoCarIU);
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }











}
