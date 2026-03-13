package com.yunus.mapper;


import com.yunus.dto.DtoCar;
import com.yunus.dto.DtoCarIU;
import com.yunus.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {

    DtoCar toDto(Car car);
    Car toEntity(DtoCarIU dtoCarIU);
}
