package com.yunus.controller;

import com.yunus.dto.DtoSoldCar;
import com.yunus.dto.DtoSoldCarIU;
import com.yunus.service.SoldCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/SOLD-car")
@RequiredArgsConstructor
public class SoldCarController {


    private final SoldCarService soldCarService;


    @PostMapping("/buy")
    public DtoSoldCar saveSOLDCar(@RequestBody @Valid DtoSoldCarIU dtoSoldCarIU) {
        return soldCarService.buyCar(dtoSoldCarIU);
    }


}
