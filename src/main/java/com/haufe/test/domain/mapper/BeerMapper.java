package com.haufe.test.domain.mapper;

import com.haufe.test.domain.Beer;
import com.haufe.test.dto.BeerDto;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeerMapper {

    @Mapping(source = "manufacturer.id", target = "manufacturerDetails.id")
    @Mapping(source = "manufacturer.name", target = "manufacturerDetails.name")
    @Mapping(source = "manufacturer.country", target = "manufacturerDetails.country")
    BeerDto toDto(Beer beer);

    @InheritInverseConfiguration
    Beer toEntity(BeerDto beerDto);

    List<BeerDto> toDtoList(List<Beer> beers);

}
