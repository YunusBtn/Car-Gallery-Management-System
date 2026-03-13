package com.yunus.mapper;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.model.Gallerist;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GalleristMapper {

    Gallerist toEntity(DtoGalleristIU dtoGalleristIU);

    DtoGallerist toDto(Gallerist gallerist);


}
