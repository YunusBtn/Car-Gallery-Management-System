package com.yunus.mapper;

import com.yunus.dto.DtoCustomer;
import com.yunus.dto.DtoCustomerIU;
import com.yunus.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(DtoCustomerIU dtoCustomerIU);

    DtoCustomer toDto(Customer customer);









}
