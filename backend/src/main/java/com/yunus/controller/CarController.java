package com.yunus.controller;

import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/save")
    public DtoCar saveCar(@Valid @RequestBody DtoCarIU dtoCarIU) {
        return carService.saveCar(dtoCarIU);
    }

    @GetMapping("/list")
    public List<DtoCar> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public DtoCar getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCarById(@PathVariable Long id) {
        carService.deleteCarById(id);
    }
}
