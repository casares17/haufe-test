package com.haufe.test.repository.specification;

import com.haufe.test.domain.Beer;
import com.haufe.test.domain.BeerType;
import org.springframework.data.jpa.domain.Specification;

public class BeerSearchSpecification {

    private BeerSearchSpecification() {
    }

    public static Specification<Beer> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Beer> hasType(BeerType beerType) {
        return (root, query, cb) ->
                beerType == null ? null : cb.equal(root.get("beerType"), beerType);
    }

    public static Specification<Beer> abvGreaterThanOrEqual(Double minAbv) {
        return (root, query, cb) ->
                minAbv == null ? null : cb.greaterThanOrEqualTo(root.get("alcoholByVolume"), minAbv);
    }

    public static Specification<Beer> abvLessThanOrEqual(Double maxAbv) {
        return (root, query, cb) ->
                maxAbv == null ? null : cb.lessThanOrEqualTo(root.get("alcoholByVolume"), maxAbv);
    }

    public static Specification<Beer> manufacturerNameContains(String manufacturerName) {
        return (root, query, cb) ->
                manufacturerName == null ? null :
                        cb.like(cb.lower(root.join("manufacturer").get("name")),
                                "%" + manufacturerName.toLowerCase() + "%");
    }
}
