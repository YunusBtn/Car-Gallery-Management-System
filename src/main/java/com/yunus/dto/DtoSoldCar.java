package com.yunus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoSoldCar extends DtoBase{

	private DtoCustomer customer;
	
	private DtoGallerist gallerist;
	
	private DtoCar car;
}
