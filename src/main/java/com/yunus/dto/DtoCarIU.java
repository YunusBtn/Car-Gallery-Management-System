package com.yunus.dto;

import com.yunus.enums.CarStatusType;
import com.yunus.enums.CurrencyType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DtoCarIU {

	@NotNull
	private String plaka;
	
	@NotNull
	private String brand;
	
	@NotNull
	private String model;
	
	@NotNull
	private long productionYear;
	
	@NotNull
	private BigDecimal price;
	
	@NotNull
	private CurrencyType currencyType;
	
	@NotNull
	private BigDecimal damagePrice;
	
	@NotNull
	private CarStatusType carStatusType;

}
