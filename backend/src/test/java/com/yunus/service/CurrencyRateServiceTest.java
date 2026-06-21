package com.yunus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunus.dto.CurrencyRatesItems;
import com.yunus.dto.CurrencyRatesResponse;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("CurrencyRateService Unit Testleri")
class CurrencyRateServiceTest {

    private CurrencyRateService currencyRateService;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        currencyRateService = new CurrencyRateService(builder);

        // Value annotation injection simulation via Reflection
        ReflectionTestUtils.setField(currencyRateService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(currencyRateService, "baseUrl", "https://evds2.tcmb.gov.tr/service/evds");
        ReflectionTestUtils.setField(currencyRateService, "series", "TP.DK.USD.A.YTL");

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("getCurrencyRates: API başarılı yanıt döndüğünde veri doğru çözümlenmeli")
    void getCurrencyRates_shouldReturnResponse_whenApiCallIsSuccessful() throws Exception {
        // Given
        CurrencyRatesItems item = new CurrencyRatesItems();
        item.setDate("21-06-2026");
        item.setUsd("38.50");

        CurrencyRatesResponse mockResponse = new CurrencyRatesResponse();
        mockResponse.setTotalCount(1);
        mockResponse.setItems(List.of(item));

        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        mockServer.expect(requestTo("https://evds2.tcmb.gov.tr/service/evds/series=TP.DK.USD.A.YTL&startDate=21-06-2026&endDate=21-06-2026&type=json"))
                .andExpect(header("key", "test-api-key"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        CurrencyRatesResponse result = currencyRateService.getCurrencyRates("21-06-2026", "21-06-2026");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getItems().size());
        assertEquals("38.50", result.getItems().get(0).getUsd());
        mockServer.verify();
    }

    @Test
    @DisplayName("getCurrencyRates: API null döndüğünde CURRENCY_RATES_IS_OCCURRED fırlatılmalı")
    void getCurrencyRates_shouldThrowException_whenResponseIsNull() {
        mockServer.expect(requestTo("https://evds2.tcmb.gov.tr/service/evds/series=TP.DK.USD.A.YTL&startDate=21-06-2026&endDate=21-06-2026&type=json"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        BaseException exception = assertThrows(BaseException.class, () ->
                currencyRateService.getCurrencyRates("21-06-2026", "21-06-2026"));

        assertEquals(ErrorType.CURRENCY_RATES_IS_OCCURRED, exception.getErrorType());
        mockServer.verify();
    }

    @Test
    @DisplayName("getCurrencyRates: API boş liste döndüğünde CURRENCY_RATES_IS_OCCURRED fırlatılmalı")
    void getCurrencyRates_shouldThrowException_whenItemsIsEmpty() throws Exception {
        CurrencyRatesResponse mockResponse = new CurrencyRatesResponse();
        mockResponse.setTotalCount(0);
        mockResponse.setItems(Collections.emptyList());

        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        mockServer.expect(requestTo("https://evds2.tcmb.gov.tr/service/evds/series=TP.DK.USD.A.YTL&startDate=21-06-2026&endDate=21-06-2026&type=json"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        BaseException exception = assertThrows(BaseException.class, () ->
                currencyRateService.getCurrencyRates("21-06-2026", "21-06-2026"));

        assertEquals(ErrorType.CURRENCY_RATES_IS_OCCURRED, exception.getErrorType());
        mockServer.verify();
    }
}
