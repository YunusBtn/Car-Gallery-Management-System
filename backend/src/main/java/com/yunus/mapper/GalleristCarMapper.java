package com.yunus.mapper;

import com.yunus.dto.DtoGalleristCar;
import com.yunus.dto.DtoGalleristCarIU;
import com.yunus.model.GalleristCar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GalleristCarMapper {

    GalleristCar toEntity(DtoGalleristCarIU dtoGalleristCarIU);
    DtoGalleristCar toDto(GalleristCar galleristCar);


}
