package com.haufe.test.controller;

import com.haufe.test.dto.ManufacturerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.service.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @Operation(summary = "Get all manufacturers")
    @GetMapping("")
    public ResponseEntity<List<ManufacturerDto>> getAllManufacturers() {
        return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.getAllManufacturers());
    }

    @Operation(summary = "Get manufacturer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerDto> getManufacturerById(@PathVariable Integer id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.getManufacturerById(id));
    }

    @Operation(summary = "Create a new manufacturer")
    @PostMapping("")
    public ResponseEntity<ManufacturerDto> createManufacturer(@RequestBody ManufacturerDto manufacturerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerService.createManufacturer(manufacturerDto));
    }

    @Operation(summary = "Update an existing manufacturer")
    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerDto> updateManufacturer(@PathVariable Integer id, @RequestBody ManufacturerDto manufacturerDto) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.updateManufacturer(id, manufacturerDto));
    }

    @Operation(summary = "Delete a manufacturer by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturer(@PathVariable Integer id) throws NotFoundException {
        manufacturerService.deleteManufacturer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
