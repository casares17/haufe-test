package com.haufe.test.controller;

import com.haufe.test.dto.BeerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.exception.ServerException;
import com.haufe.test.service.BeerService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beers")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;

    @Operation(summary = "Get all beers with optional sorting")
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BeerDto>> getAllBeers(@RequestParam(defaultValue = "name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.status(HttpStatus.OK).body(beerService.getAllBeers(sortBy, direction));
    }

    @Operation(summary = "Search beers with pagination and filters")
    @PostMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<BeerDto>> searchBeers(
            @RequestBody BeerSearchRequest request
    ) {
        Page<BeerDto> result = beerService.searchBeers(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get beer by ID")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(beerService.getBeerById(id));
    }

    @Operation(summary = "Create a new beer")
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeerDto> createBeer(@RequestBody BeerDto beer) throws ServerException {
        return ResponseEntity.status(HttpStatus.CREATED).body(beerService.createBeer(beer));
    }

    @Operation(summary = "Update an existing beer")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeerDto> updateBeer(@PathVariable Integer id, @RequestBody BeerDto beer) throws NotFoundException, ServerException {
        return ResponseEntity.status(HttpStatus.OK).body(beerService.updateBeer(id, beer));
    }

    @Operation(summary = "Delete a beer by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBeer(@PathVariable Integer id) throws NotFoundException {
        beerService.deleteBeer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
