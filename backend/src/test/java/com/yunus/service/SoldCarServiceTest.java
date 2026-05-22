package com.yunus.service;

import com.yunus.dto.CurrencyRatesItems;
import com.yunus.dto.CurrencyRatesResponse;
import com.yunus.dto.DtoSoldCar;
import com.yunus.dto.DtoSoldCarIU;
import com.yunus.enums.CarStatusType;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.SoldCarMapper;
import com.yunus.model.Account;
import com.yunus.model.Car;
import com.yunus.model.Customer;
import com.yunus.model.Gallerist;
import com.yunus.model.SoldCar;
import com.yunus.repository.CarRepository;
import com.yunus.repository.CustomerRepository;
import com.yunus.repository.GalleristRepository;
import com.yunus.repository.SoldCarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoldCarService Unit Testleri")
class SoldCarServiceTest {

    @InjectMocks
    private SoldCarService soldCarService;

    @Mock
    private SoldCarRepository soldCarRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CurrencyRateService currencyRateService;
    @Mock
    private GalleristRepository galleristRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private SoldCarMapper soldCarMapper;

    // Test sabitleri
    private static final Long CUSTOMER_ID  = 1L;
    private static final Long CAR_ID       = 2L;
    private static final Long GALLERIST_ID = 3L;
    private static final String USD_RATE   = "38.50";

    private DtoSoldCarIU dtoSoldCarIU;
    private Customer customer;
    private Car car;
    private Gallerist gallerist;
    private CurrencyRatesResponse currencyRatesResponse;

    @BeforeEach
    void setUp() {
        // DTO
        dtoSoldCarIU = new DtoSoldCarIU();
        dtoSoldCarIU.setCustomerId(CUSTOMER_ID);
        dtoSoldCarIU.setCarId(CAR_ID);
        dtoSoldCarIU.setGalleristId(GALLERIST_ID);

        // Account (100.000 TL bakiye)
        Account account = new Account();
        account.setAmount(new BigDecimal("100000"));

        // Customer
        customer = new Customer();
        customer.setAccount(account);

        // Car (2000 USD fiyat, SALABLE)
        car = new Car();
        car.setId(CAR_ID);
        car.setPrice(new BigDecimal("2000"));
        car.setCarStatusType(CarStatusType.SALABLE);

        // Gallerist
        gallerist = new Gallerist();
        gallerist.setId(GALLERIST_ID);

        // CurrencyRatesResponse
        CurrencyRatesItems item = new CurrencyRatesItems();
        item.setUsd(USD_RATE);

        currencyRatesResponse = new CurrencyRatesResponse();
        currencyRatesResponse.setItems(List.of(item));
    }

    // ─────────────────────────────────────────────
    // BAŞARILI AKIŞ
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("buyCar: Tüm koşullar sağlandığında başarıyla araç satışı gerçekleşmeli")
    void buyCar_shouldReturnDtoSoldCar_whenAllConditionsAreMet() {
        // Given
        SoldCar savedSoldCar = new SoldCar();
        DtoSoldCar expectedDto = new DtoSoldCar();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(currencyRatesResponse);
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.of(gallerist));
        when(soldCarRepository.save(any(SoldCar.class))).thenReturn(savedSoldCar);
        when(soldCarMapper.toDto(savedSoldCar)).thenReturn(expectedDto);

        // When
        DtoSoldCar result = soldCarService.buyCar(dtoSoldCarIU);

        // Then
        assertNotNull(result);
        assertSame(expectedDto, result);

        // Araç durumu SOLD olarak güncellenmeli
        assertEquals(CarStatusType.SOLD, car.getCarStatusType());

        // Repository kayıt çağrıları doğrulanmalı
        verify(soldCarRepository, times(1)).save(any(SoldCar.class));
        verify(carRepository, times(1)).save(car);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("buyCar: Müşteri bakiyesi araç fiyatına tam eşit olduğunda satış gerçekleşmeli")
    void buyCar_shouldSucceed_whenCustomerBalanceExactlyEqualsCarPrice() {
        // Given
        // 100.000 TL / 38.50 = 2597.40... USD — araç fiyatı tam 2597 USD olarak ayarla
        BigDecimal usdRate = new BigDecimal("38.50");
        BigDecimal tlBalance = new BigDecimal("100000");
        // customerUsdAmount = tlBalance / usdRate (HALF_UP) = ~2597.40 USD
        // car.price = 2597 → customerUsdAmount >= carPrice, yeterli
        BigDecimal customerUsdAmount = tlBalance.divide(usdRate, RoundingMode.HALF_UP);
        car.setPrice(customerUsdAmount); // tam eşit

        SoldCar savedSoldCar = new SoldCar();
        DtoSoldCar expectedDto = new DtoSoldCar();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(currencyRatesResponse);
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.of(gallerist));
        when(soldCarRepository.save(any(SoldCar.class))).thenReturn(savedSoldCar);
        when(soldCarMapper.toDto(savedSoldCar)).thenReturn(expectedDto);

        // When
        DtoSoldCar result = soldCarService.buyCar(dtoSoldCarIU);

        // Then
        assertNotNull(result);
        verify(soldCarRepository, times(1)).save(any(SoldCar.class));
    }

    @Test
    @DisplayName("buyCar: Satış sonrası müşteri kalan TL bakiyesi doğru hesaplanmalı")
    void buyCar_shouldUpdateCustomerBalanceCorrectly() {
        // Given
        // 100.000 TL / 38.50 = 2597 USD (HALF_UP)
        // Araç fiyatı = 2000 USD
        // Kalan = (2597 - 2000) * 38.50 = 597 * 38.50 = 22.984.50 TL

        SoldCar savedSoldCar = new SoldCar();
        DtoSoldCar expectedDto = new DtoSoldCar();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(currencyRatesResponse);
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.of(gallerist));
        when(soldCarRepository.save(any(SoldCar.class))).thenReturn(savedSoldCar);
        when(soldCarMapper.toDto(savedSoldCar)).thenReturn(expectedDto);

        // When
        soldCarService.buyCar(dtoSoldCarIU);

        // Then – kalan bakiye negatif olmamalı
        BigDecimal remainingBalance = customer.getAccount().getAmount();
        assertThat(remainingBalance).isGreaterThanOrEqualTo(BigDecimal.ZERO);

        // Satış sonrası customerRepository.save çağrılmalı
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertNotNull(customerCaptor.getValue().getAccount().getAmount());
    }

    // ─────────────────────────────────────────────
    // HATA AKIŞLARI
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("buyCar: Müşteri bulunamadığında NOT_FOUND hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCustomerNotFound() {
        // Given
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());

        // Sonraki adımlar çalışmamalı
        verify(carRepository, never()).findById(any());
        verify(soldCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("buyCar: Araç bulunamadığında NOT_FOUND hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCarNotFound() {
        // Given
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("buyCar: Araç zaten satılmışsa CAR_STATUS_IS_ALREADY_SOLD hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCarAlreadySold() {
        // Given
        car.setCarStatusType(CarStatusType.SOLD);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.CAR_STATUS_IS_ALREADY_SOLD, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
        verify(currencyRateService, never()).getCurrencyRates(any(), any());
    }

    @Test
    @DisplayName("buyCar: TCMB kur yanıtı null gelirse CURRENCY_RATES_IS_OCCURRED hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCurrencyRatesResponseIsNull() {
        // Given
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(null);

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.CURRENCY_RATES_IS_OCCURRED, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("buyCar: TCMB kur listesi boşsa CURRENCY_RATES_IS_OCCURRED hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCurrencyRatesItemsIsEmpty() {
        // Given
        CurrencyRatesResponse emptyResponse = new CurrencyRatesResponse();
        emptyResponse.setItems(Collections.emptyList());

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(emptyResponse);

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.CURRENCY_RATES_IS_OCCURRED, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("buyCar: TCMB kur yanıtında items null gelirse CURRENCY_RATES_IS_OCCURRED hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCurrencyRatesItemsIsNull() {
        // Given
        CurrencyRatesResponse nullItemsResponse = new CurrencyRatesResponse();
        nullItemsResponse.setItems(null);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(nullItemsResponse);

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.CURRENCY_RATES_IS_OCCURRED, ex.getErrorType());
    }

    @Test
    @DisplayName("buyCar: Galerici bulunamadığında NOT_FOUND hatası fırlatılmalı")
    void buyCar_shouldThrow_whenGalleristNotFound() {
        // Given
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(currencyRatesResponse);
        when(galleristRepository.findById(GALLERIST_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
    }

    @Test
    @DisplayName("buyCar: Müşteri bakiyesi yetersizse MONEY_ERROR hatası fırlatılmalı")
    void buyCar_shouldThrow_whenCustomerHasInsufficientBalance() {
        // Given – 500 TL / 38.50 = ~12.98 USD < 2000 USD (araç fiyatı)
        customer.getAccount().setAmount(new BigDecimal("500"));

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(currencyRateService.getCurrencyRates(any(), any())).thenReturn(currencyRatesResponse);

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> soldCarService.buyCar(dtoSoldCarIU));
        assertEquals(ErrorType.MONEY_ERROR, ex.getErrorType());
        verify(soldCarRepository, never()).save(any());
        verify(carRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // getAllSoldCars
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllSoldCars: Hiç satılmış araç yoksa boş liste dönmeli")
    void getAllSoldCars_shouldReturnEmptyList_whenNoSoldCars() {
        // Given
        when(soldCarRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DtoSoldCar> result = soldCarService.getAllSoldCars();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllSoldCars: Satılmış araçlar varsa eşlenmiş DTO listesi dönmeli")
    void getAllSoldCars_shouldReturnMappedList() {
        // Given
        SoldCar soldCar1 = new SoldCar();
        SoldCar soldCar2 = new SoldCar();
        DtoSoldCar dto1 = new DtoSoldCar();
        DtoSoldCar dto2 = new DtoSoldCar();

        when(soldCarRepository.findAll()).thenReturn(List.of(soldCar1, soldCar2));
        when(soldCarMapper.toDto(soldCar1)).thenReturn(dto1);
        when(soldCarMapper.toDto(soldCar2)).thenReturn(dto2);

        // When
        List<DtoSoldCar> result = soldCarService.getAllSoldCars();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result).containsExactly(dto1, dto2);
    }
}
