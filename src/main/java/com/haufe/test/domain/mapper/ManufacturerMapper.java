package com.haufe.test.domain.mapper;

import com.haufe.test.domain.Manufacturer;
import com.haufe.test.dto.ManufacturerDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManufacturerMapper {


    ManufacturerDto toDto(Manufacturer manufacturer);

    Manufacturer toEntity(ManufacturerDto manufacturerDto);

    List<ManufacturerDto> toDtoList(List<Manufacturer> manufacturers);

}
