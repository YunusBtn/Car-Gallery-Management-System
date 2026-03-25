package com.yunus.mapper;

import com.yunus.dto.DtoSoldCar;
import com.yunus.model.SoldCar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SoldCarMapper {

    DtoSoldCar toDto(SoldCar soldCar);
}
