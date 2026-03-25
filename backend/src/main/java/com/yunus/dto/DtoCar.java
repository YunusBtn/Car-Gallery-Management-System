package com.yunus.dto;

import com.yunus.enums.CarStatusType;
import com.yunus.enums.CurrencyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DtoCar extends DtoBase{

	private String plaka;

	private String brand;

	private String model;

	private long productionYear;

	private BigDecimal price;

	private CurrencyType currencyType;

	private BigDecimal damagePrice;

	private CarStatusType carStatusType;
}
