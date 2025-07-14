package com.haufe.test.domain;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum BeerSortField {
    NAME("name"),
    ABV("alcoholByVolume"),
    TYPE("beerType"),
    MANUFACTURER("manufacturerId");

    private final String field;

    BeerSortField(String field) {
        this.field = field;
    }

    public static Optional<BeerSortField> from(String value) {
        return Arrays.stream(values())
                .filter(f -> f.name().equalsIgnoreCase(value) || f.field.equalsIgnoreCase(value))
                .findFirst();
    }
}
