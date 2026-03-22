package com.yunus.controller;

import com.yunus.dto.DtoSaledCar;
import com.yunus.dto.DtoSaledCarIU;
import com.yunus.service.SaledCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/saled-car")
@RequiredArgsConstructor
public class SaledCarController {


    private final SaledCarService saledCarService;


    @PostMapping("/buy")
    public DtoSaledCar saveSaledCar(@RequestBody @Valid DtoSaledCarIU dtoSaledCarIU) {
        return saledCarService.buyCar(dtoSaledCarIU);
    }


}
