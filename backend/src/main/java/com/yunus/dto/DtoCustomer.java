package com.yunus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DtoCustomer extends DtoBase{

	private String firstName;
	
	private String lastName;
	
	private String tckn;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate birthOfDate;
	
	private DtoAddress address;
	
	private DtoAccount  account;
}
