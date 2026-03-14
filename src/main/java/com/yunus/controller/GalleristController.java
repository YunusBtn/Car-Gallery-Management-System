package com.yunus.controller;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.service.GalleristService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gallerist")
@RequiredArgsConstructor
public class GalleristController {

    private final GalleristService galleristService;

    @PostMapping("/save")
    public DtoGallerist saveGallerist(@Valid @RequestBody DtoGalleristIU dtoGalleristIU) {
        return galleristService.saveGallerist(dtoGalleristIU);
    }


}
