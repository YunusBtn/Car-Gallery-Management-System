package com.yunus.service;

import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.CarMapper;
import com.yunus.model.Car;
import com.yunus.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @CacheEvict(value = "cars", allEntries = true)
    public DtoCar saveCar(DtoCarIU dtoCarIU) {
        Car car = carMapper.toEntity(dtoCarIU);
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Cacheable(value = "cars", key = "'all'")
    public List<DtoCar> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "cars", key = "#id")
    public DtoCar getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return carMapper.toDto(car);
    }

    @CacheEvict(value = "cars", allEntries = true)
    public void deleteCarById(Long id) {
        if (!carRepository.existsById(id)) {
            throw new BaseException(ErrorType.NOT_FOUND, id.toString());
        }
        carRepository.deleteById(id);
    }
}
