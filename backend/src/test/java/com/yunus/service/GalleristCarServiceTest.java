package com.yunus.service;

import com.yunus.dto.DtoGalleristCar;
import com.yunus.dto.DtoGalleristCarIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.GalleristCarMapper;
import com.yunus.model.Car;
import com.yunus.model.Gallerist;
import com.yunus.model.GalleristCar;
import com.yunus.repository.CarRepository;
import com.yunus.repository.GalleristCarRepository;
import com.yunus.repository.GalleristRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GalleristCarService Unit Testleri")
class GalleristCarServiceTest {

    @InjectMocks
    private GalleristCarService galleristCarService;

    @Mock private GalleristCarRepository galleristCarRepository;
    @Mock private GalleristCarMapper galleristCarMapper;
    @Mock private CarRepository carRepository;
    @Mock private GalleristRepository galleristRepository;

    private static final Long GALLERIST_ID = 1L;
    private static final Long CAR_ID       = 2L;

    private DtoGalleristCarIU dtoGalleristCarIU;
    private Gallerist gallerist;
    private Car car;
    private GalleristCar galleristCar;
    private DtoGalleristCar dtoGalleristCar;

    @BeforeEach
    void setUp() {
        dtoGalleristCarIU = new DtoGalleristCarIU();
        dtoGalleristCarIU.setGalleristId(GALLERIST_ID);
        dtoGalleristCarIU.setCarId(CAR_ID);

        gallerist = new Gallerist();
        gallerist.setId(GALLERIST_ID);
        gallerist.setFirstName("Ali");

        car = new Car();
        car.setId(CAR_ID);
        car.setBrand("BMW");

        galleristCar = new GalleristCar();
        galleristCar.setGallerist(gallerist);
        galleristCar.setCar(car);

        dtoGalleristCar = new DtoGalleristCar();
    }

    // ─────────────────────────────────────────────
    // saveGalleristCar
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveGalleristCar: Galerici ve araç mevcutsa kaydedilmeli ve DTO dönmeli")
    void saveGalleristCar_shouldReturnDto_whenBothFound() {
        // Given
        GalleristCar emptyGalleristCar = new GalleristCar();

        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.of(gallerist));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(galleristCarMapper.toEntity(dtoGalleristCarIU)).thenReturn(emptyGalleristCar);
        when(galleristCarRepository.save(emptyGalleristCar)).thenReturn(galleristCar);
        when(galleristCarMapper.toDto(galleristCar)).thenReturn(dtoGalleristCar);

        // When
        DtoGalleristCar result = galleristCarService.saveGalleristCar(dtoGalleristCarIU);

        // Then
        assertNotNull(result);
        verify(galleristCarRepository, times(1)).save(emptyGalleristCar);

        // Araç ve galerici entity'nin set edildiği doğrulanmalı
        assertEquals(car, emptyGalleristCar.getCar());
        assertEquals(gallerist, emptyGalleristCar.getGallerist());
    }

    @Test
    @DisplayName("saveGalleristCar: Galerici bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveGalleristCar_shouldThrow_whenGalleristNotFound() {
        // Given
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> galleristCarService.saveGalleristCar(dtoGalleristCarIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(carRepository, never()).findById(any());
        verify(galleristCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveGalleristCar: Araç bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveGalleristCar_shouldThrow_whenCarNotFound() {
        // Given
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.of(gallerist));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> galleristCarService.saveGalleristCar(dtoGalleristCarIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(galleristCarRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // getAllGalleristCars
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllGalleristCars: Galerici araçları varsa DTO listesi dönmeli")
    void getAllGalleristCars_shouldReturnList() {
        // Given
        GalleristCar gc2 = new GalleristCar();
        DtoGalleristCar dto2 = new DtoGalleristCar();

        when(galleristCarRepository.findAll()).thenReturn(List.of(galleristCar, gc2));
        when(galleristCarMapper.toDto(galleristCar)).thenReturn(dtoGalleristCar);
        when(galleristCarMapper.toDto(gc2)).thenReturn(dto2);

        // When
        List<DtoGalleristCar> result = galleristCarService.getAllGalleristCars();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result).containsExactly(dtoGalleristCar, dto2);
    }

    @Test
    @DisplayName("getAllGalleristCars: Kayıt yoksa boş liste dönmeli")
    void getAllGalleristCars_shouldReturnEmptyList() {
        // Given
        when(galleristCarRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DtoGalleristCar> result = galleristCarService.getAllGalleristCars();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
