package com.yunus.mapper;

import com.yunus.dto.DtoAccount;
import com.yunus.dto.DtoAccountIU;
import com.yunus.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(DtoAccountIU dtoAccountIU);

    DtoAccount toDto(Account account);


}


