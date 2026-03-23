package com.yunus.controller;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.service.GalleristService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallerist")
@RequiredArgsConstructor
public class GalleristController {

    private final GalleristService galleristService;

    @PostMapping("/save")
    public DtoGallerist saveGallerist(@Valid @RequestBody DtoGalleristIU dtoGalleristIU) {
        return galleristService.saveGallerist(dtoGalleristIU);
    }

    @GetMapping("/list")
    public List<DtoGallerist> getAllGallerists() {
        return galleristService.getAllGallerists();
    }

    @GetMapping("/{id}")
    public DtoGallerist getGalleristById(@PathVariable Long id) {
        return galleristService.getGalleristById(id);
    }
}
