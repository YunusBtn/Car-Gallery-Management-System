package com.yunus.service;

import com.yunus.dto.CurrencyRatesResponse;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class CurrencyRateService {

    private static final String RESPONSE_TYPE = "json";

    @Value("${tcmb.api.key}")
    private String apiKey;

    @Value("${tcmb.api.base-url}")
    private String baseUrl;

    @Value("${tcmb.api.series}")
    private String series;


    private final RestClient restClient;


    public CurrencyRateService() {
        this.restClient = RestClient.builder().build();
    }


    public CurrencyRatesResponse getCurrencyRates(String startDate, String endDate) {

        String url = buildUrl(startDate, endDate);
        log.info("TCMB kur isteği gönderiliyor: {}", url);

        CurrencyRatesResponse response = restClient.get()
                .uri(url)
                .header("key", apiKey)
                .retrieve()
                .body(CurrencyRatesResponse.class);


        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            log.warn("TCMB'den boş veya null yanıt döndü", startDate, endDate);

            throw new BaseException(ErrorType.CURRENCY_RATES_IS_OCCURRED, "Kur Boş döndü,Tatil oalbilir.");
        }

        log.info("Kur verisi başarıyla alındı.");


        return response;

    }


    private String buildUrl(String startDate, String endDate) {
        return baseUrl
                + "/series=" + series
                + "&startDate=" + startDate
                + "&endDate=" + endDate
                + "&type=" + RESPONSE_TYPE;
    }


}
