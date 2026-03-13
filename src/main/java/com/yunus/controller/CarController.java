package com.yunus.controller;

import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/save")
    public DtoCar saveCar(@Valid @RequestBody DtoCarIU dtoCarIU) {
        return carService.saveCar(dtoCarIU);
    }
}
