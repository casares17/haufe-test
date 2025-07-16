package com.haufe.test.controller;

import com.haufe.test.domain.BeerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeerSearchRequest {
    private String name;
    private BeerType beerType;
    private Double minAbv;
    private Double maxAbv;
    private String manufacturerName;

    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private String sortBy = "name";
    @Builder.Default
    private String direction = "asc";
}
