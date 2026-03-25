package com.yunus.mapper;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    DtoAddress toDto(Address Address);

    Address toEntity(DtoAddressIU dtoAddressIU);


}
