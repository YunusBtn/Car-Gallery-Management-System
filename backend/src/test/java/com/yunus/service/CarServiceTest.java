package com.yunus.service;

import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.enums.CarStatusType;
import com.yunus.enums.CurrencyType;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.CarMapper;
import com.yunus.model.Car;
import com.yunus.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarService Unit Testleri")
class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    private DtoCarIU dtoCarIU;
    private Car car;
    private DtoCar dtoCar;

    @BeforeEach
    void setUp() {
        dtoCarIU = new DtoCarIU();
        dtoCarIU.setPlaka("34ABC123");
        dtoCarIU.setBrand("Toyota");
        dtoCarIU.setModel("Corolla");
        dtoCarIU.setProductionYear(2022);
        dtoCarIU.setPrice(new BigDecimal("25000"));
        dtoCarIU.setCurrencyType(CurrencyType.USD);
        dtoCarIU.setDamagePrice(BigDecimal.ZERO);
        dtoCarIU.setCarStatusType(CarStatusType.SALABLE);

        car = new Car();
        car.setId(1L);
        car.setPlaka("34ABC123");
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setPrice(new BigDecimal("25000"));
        car.setCarStatusType(CarStatusType.SALABLE);

        dtoCar = new DtoCar();
        dtoCar.setId(1L);
        dtoCar.setPlaka("34ABC123");
        dtoCar.setBrand("Toyota");
    }

    // ─────────────────────────────────────────────
    // saveCar
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveCar: Geçerli DTO ile araç kaydedilmeli ve DtoCar dönmeli")
    void saveCar_shouldReturnDtoCar() {
        // Given
        when(carMapper.toEntity(dtoCarIU)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(dtoCar);

        // When
        DtoCar result = carService.saveCar(dtoCarIU);

        // Then
        assertNotNull(result);
        assertEquals("34ABC123", result.getPlaka());
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    @DisplayName("saveCar: Mapper ve repository doğru sırayla çağrılmalı")
    void saveCar_shouldCallMapperAndRepositoryInOrder() {
        // Given
        when(carMapper.toEntity(dtoCarIU)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(dtoCar);

        // When
        carService.saveCar(dtoCarIU);

        // Then
        var inOrder = inOrder(carMapper, carRepository);
        inOrder.verify(carMapper).toEntity(dtoCarIU);
        inOrder.verify(carRepository).save(car);
        inOrder.verify(carMapper).toDto(car);
    }

    // ─────────────────────────────────────────────
    // getAllCars
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllCars: Araç yoksa boş liste dönmeli")
    void getAllCars_shouldReturnEmptyList() {
        // Given
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DtoCar> result = carService.getAllCars();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllCars: Araçlar varsa eşlenmiş DTO listesi dönmeli")
    void getAllCars_shouldReturnMappedList() {
        // Given
        Car car2 = new Car();
        car2.setId(2L);
        DtoCar dtoCar2 = new DtoCar();
        dtoCar2.setId(2L);

        when(carRepository.findAll()).thenReturn(List.of(car, car2));
        when(carMapper.toDto(car)).thenReturn(dtoCar);
        when(carMapper.toDto(car2)).thenReturn(dtoCar2);

        // When
        List<DtoCar> result = carService.getAllCars();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result).containsExactlyInAnyOrder(dtoCar, dtoCar2);
    }

    // ─────────────────────────────────────────────
    // getCarById
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getCarById: Araç bulunduğunda DtoCar dönmeli")
    void getCarById_shouldReturnDtoCar_whenFound() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(dtoCar);

        // When
        DtoCar result = carService.getCarById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getCarById: Araç bulunamadığında NOT_FOUND hatası fırlatılmalı")
    void getCarById_shouldThrow_whenNotFound() {
        // Given
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> carService.getCarById(99L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(carMapper, never()).toDto(any());
    }
}
