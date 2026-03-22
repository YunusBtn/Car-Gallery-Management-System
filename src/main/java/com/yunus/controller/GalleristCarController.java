package com.yunus.controller;

import com.yunus.dto.DtoGalleristCar;
import com.yunus.dto.DtoGalleristCarIU;
import com.yunus.service.GalleristCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gallerist-car")
@RequiredArgsConstructor
public class GalleristCarController {

    private final GalleristCarService galleristCarService;


    @RequestMapping("/save")
    public DtoGalleristCar saveGalleristCar(@Valid @RequestBody DtoGalleristCarIU dtoGalleristCarIU) {
        return galleristCarService.saveGalleristCar(dtoGalleristCarIU);
    }
}
