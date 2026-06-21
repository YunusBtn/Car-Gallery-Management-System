package com.yunus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CurrencyRatesItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("Tarih")
    private String date;

    @JsonProperty("TP_DK_USD_A_YTL")
    private String usd;


}
