package com.yunus.controller;

import com.yunus.dto.DtoSoldCar;
import com.yunus.dto.DtoSoldCarIU;
import com.yunus.service.SoldCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sold-car")
@RequiredArgsConstructor
public class SoldCarController {

    private final SoldCarService soldCarService;

    @PostMapping("/buy")
    public DtoSoldCar buyCar(@RequestBody @Valid DtoSoldCarIU dtoSoldCarIU) {
        return soldCarService.buyCar(dtoSoldCarIU);
    }

    @GetMapping("/list")
    public List<DtoSoldCar> getAllSoldCars() {
        return soldCarService.getAllSoldCars();
    }
}
