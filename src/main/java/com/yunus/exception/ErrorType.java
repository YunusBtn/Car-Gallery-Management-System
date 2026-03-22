package com.yunus.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    NOT_FOUND("Kayıt Bulunamadı", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("Geçersiz Veri Girdiniz", HttpStatus.BAD_REQUEST),
    DUPLICATE_ENTRY("Kayıt Zaten Mevcut", HttpStatus.CONFLICT),
    ACCESS_DENIED("Yetkiniz Yok", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR("Sistem Hatası", HttpStatus.INTERNAL_SERVER_ERROR),
    CURRENCY_RATES_IS_OCCURED("Kur satış Hatası",HttpStatus.INTERNAL_SERVER_ERROR),
    CAR_STATUS_IS_ALREADY_SOLD("Araç zaten satılmış",HttpStatus.BAD_REQUEST),
    MONEY_ERROR("Bakiye Yetersiz", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorType(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
