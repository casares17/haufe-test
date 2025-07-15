package com.haufe.test.repository;

import com.haufe.test.domain.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BeerRepository extends JpaRepository<Beer, Integer>, JpaSpecificationExecutor<Beer> {
}
