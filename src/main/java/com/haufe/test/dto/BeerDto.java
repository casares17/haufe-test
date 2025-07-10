package com.haufe.test.dto;

import com.haufe.test.domain.BeerType;
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
public class BeerDto {

    private Integer id;
    private String name;
    private Double alcoholByVolume;
    private BeerType beerType;
    private String description;
    private ManufacturerDetails manufacturerDetails;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManufacturerDetails{
        private Integer id;
        private String name;
        private String country;
    }
}
