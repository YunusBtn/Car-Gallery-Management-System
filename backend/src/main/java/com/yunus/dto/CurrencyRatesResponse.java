package com.yunus.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class CurrencyRatesResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer totalCount;
    private List<CurrencyRatesItems> items;


}
