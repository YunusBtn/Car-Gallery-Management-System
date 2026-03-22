package com.yunus.service;

import com.yunus.dto.CurrencyRatesResponse;
import com.yunus.dto.DtoSoldCar;
import com.yunus.dto.DtoSoldCarIU;
import com.yunus.enums.CarStatusType;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.SoldCarMapper;
import com.yunus.model.Car;
import com.yunus.model.Customer;
import com.yunus.model.SoldCar;
import com.yunus.repository.CarRepository;
import com.yunus.repository.CustomerRepository;
import com.yunus.repository.GalleristRepository;
import com.yunus.repository.SoldCarRepository;
import com.yunus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Bu süreçte şu işlemleri gerçekleştirir:
 * 1. Müşteri ve araç varlık kontrolü (DB'de kayıtlı mı?)
 * 2. Araç müsaitlik kontrolü (daha önce satılmış mı?)
 * 3. TCMB'den güncel USD kuru çekimi (tek seferlik)
 * 4. Müşterinin TL bakiyesini USD'ye çevirip araç fiyatıyla karşılaştırma
 * 5. Satış kaydı (SOLDCar) oluşturup veritabanına kaydetme
 * 6. Araç durumunu SOLD olarak güncelleme
 * 7. Müşterinin bakiyesinden araç bedelini düşüp TL olarak geri yazma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SoldCarService {

    private final SoldCarRepository soldCarRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyRateService currencyRateService;
    private final GalleristRepository galleristRepository;
    private final CarRepository carRepository;
    private final SoldCarMapper soldCarMapper;


    private BigDecimal fetchCurrentUsdRate() {

        String today = DateUtils.getCurrentyDate();

        CurrencyRatesResponse response = currencyRateService.getCurrencyRates(today, today);

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {

            throw new BaseException(ErrorType.CURRENCY_RATES_IS_OCCURED, "Kur verisi alınamadı...");
        }

        String usdValue = response.getItems().get(0).getUsd();
        log.info("Usd Kuru çekildi : {}", usdValue);

        return new BigDecimal(usdValue);

    }

    private BigDecimal convertToUsd(Customer customer, BigDecimal usdRate) {
        return customer.getAccount().getAmount().divide(usdRate, RoundingMode.HALF_UP);

    }

    private BigDecimal calculateRemainingTlAmount(BigDecimal carPrice, BigDecimal usdRate, BigDecimal customerUsdAmount) {
        BigDecimal remainingUsd = customerUsdAmount.subtract(carPrice);

        return remainingUsd.multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isCarAvailable(Car car) {
        return !CarStatusType.SOLD.equals(car.getCarStatusType());
    }


    private boolean hasEnoughBalance(BigDecimal customerUsdAmount, BigDecimal carPrice) {
        return customerUsdAmount.compareTo(carPrice) >= 0;
    }


    private SoldCar buildSOLDCar(DtoSoldCarIU dtoSoldCarIU, Car car, Customer customer) {

        SoldCar soldCar = new SoldCar();
        soldCar.setCreateTime(LocalDateTime.now());
        soldCar.setCar(car);
        soldCar.setCustomer(customer);

        soldCar.setGallerist(galleristRepository.findById(dtoSoldCarIU.getGalleristId())
                .orElseThrow(() ->
                        new BaseException(
                                ErrorType.NOT_FOUND, dtoSoldCarIU.getGalleristId().toString())));

        return soldCar;
    }

    @Transactional
    public DtoSoldCar buyCar(DtoSoldCarIU dtoSoldCarIU) {

        //Adım 1
        Customer customer = customerRepository.findById(dtoSoldCarIU.getCustomerId())
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, dtoSoldCarIU.getCustomerId().toString()));

        Car car = carRepository.findById(dtoSoldCarIU.getCarId())
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "SOLD Car not found"));


        //Adım 2
        if (!isCarAvailable(car)) {
            throw new BaseException(ErrorType.CAR_STATUS_IS_ALREADY_SOLD, "Car status is already SOLD");
        }

        //adim 3

        BigDecimal usdRate = fetchCurrentUsdRate();

        //adım 4

        BigDecimal customerUsdAmount = convertToUsd(customer, usdRate);

        if (!hasEnoughBalance(customerUsdAmount, car.getPrice())) {
            throw new BaseException(ErrorType.MONEY_ERROR, "Customer usd amount is not enough");
        }

        //adım 5
        SoldCar savedSoldCar = soldCarRepository.save(
                buildSOLDCar(dtoSoldCarIU, car, customer));

        //adım 6
        car.setCarStatusType(CarStatusType.SOLD);
        carRepository.save(car);

        //adım 7

        BigDecimal remainingTl = calculateRemainingTlAmount(
                car.getPrice(), usdRate, customerUsdAmount);

        customer.getAccount().setAmount(remainingTl);
        customerRepository.save(customer);

        log.info("Araç satışı tamamlandı: {}", remainingTl, car.getId(), customer.getId());


        return soldCarMapper.toDto(savedSoldCar);
    }


}

