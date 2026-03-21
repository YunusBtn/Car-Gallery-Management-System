package com.yunus.mapper;

import com.yunus.dto.DtoSaledCar;
import com.yunus.model.SaledCar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaledCarMapper {

    DtoSaledCar toDto(SaledCar saledCar);
}
