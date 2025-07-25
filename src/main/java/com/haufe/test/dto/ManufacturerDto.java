package com.haufe.test.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerDto {

    private Integer id;
    private String name;
    private String country;

    List<BeerDto> beers;

}
