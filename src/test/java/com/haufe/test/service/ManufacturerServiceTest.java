package com.haufe.test.service;

import com.haufe.test.domain.Manufacturer;
import com.haufe.test.domain.mapper.ManufacturerMapper;
import com.haufe.test.dto.ManufacturerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.repository.ManufacturerRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    private final ManufacturerMapper manufacturerMapper = Mappers.getMapper(ManufacturerMapper.class);

    private ManufacturerService manufacturerService;
    private Manufacturer manufacturer1;
    private Manufacturer manufacturer2;


    @BeforeEach
    void setUp() {
        manufacturer1 = Manufacturer.builder().id(1).name("Test Manufacturer 1").country("Spain").build();
        manufacturer2 = Manufacturer.builder().id(2).name("Test Manufacturer 2").country("Portugal").build();

        manufacturerService = new ManufacturerService(manufacturerRepository, manufacturerMapper);
    }

    @Test
    void testGetAllManufacturers() {
        when(manufacturerRepository.findAll()).thenReturn(List.of(manufacturer1, manufacturer2));

        var result = manufacturerService.getAllManufacturers();

        assertThat(result).size().isEqualTo(2);
        verify(manufacturerRepository).findAll();
    }

    @Test
    void testGetManufacturerById() throws NotFoundException {

        when(manufacturerRepository.findById(1)).thenReturn(Optional.of(manufacturer1));

        var result = manufacturerService.getManufacturerById(1);

        assertThat(result.getId()).isEqualTo(manufacturer1.getId());
        assertThat(result.getName()).isEqualTo("Test Manufacturer");
        assertThat(result.getCountry()).isEqualTo("Spain");
        verify(manufacturerRepository, times(1)).findById(1);
    }

    @Test
    void testGetManufacturerById_NotFound() {
        when(manufacturerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> manufacturerService.getManufacturerById(1));
    }

    @Test
    void testCreateManufacturer() {

        when(manufacturerRepository.save(any())).thenReturn(manufacturer1);
        ManufacturerDto manufacturerDto = ManufacturerDto.builder()
                .name("Test Manufacturer 1")
                .build();

        var result = manufacturerService.createManufacturer(manufacturerDto);

        verify(manufacturerRepository).save(any());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Manufacturer 1");
    }

    @Test
    void testUpdateManufacturer() throws NotFoundException {

        when(manufacturerRepository.findById(1)).thenReturn(Optional.of(manufacturer1));
        when(manufacturerRepository.save(any())).thenReturn(manufacturer1);
        ManufacturerDto manufacturerDto = ManufacturerDto.builder()
                .name("Updated Manufacturer")
                .country("Updated Country")
                .build();

        var result = manufacturerService.updateManufacturer(1, manufacturerDto);

        verify(manufacturerRepository, times(1)).findById(1);
        assertThat(result.getName()).isEqualTo("Updated Manufacturer");
        assertThat(result.getCountry()).isEqualTo("Updated Country");
    }

    @Test
    void testUpdateManufacturer_NotFound() {

        when(manufacturerRepository.findById(99)).thenReturn(Optional.empty());

        ManufacturerDto manufacturerDto = ManufacturerDto.builder()
                .name("Updated Manufacturer")
                .country("Updated Country")
                .build();
        assertThrows(NotFoundException.class, () -> manufacturerService.updateManufacturer(99, manufacturerDto));
    }

    @Test
    void testDeleteManufacturer() throws NotFoundException {

        when(manufacturerRepository.findById(1)).thenReturn(Optional.of(manufacturer1));

        manufacturerService.deleteManufacturer(1);

        verify(manufacturerRepository, times(1)).findById(1);
        verify(manufacturerRepository, times(1)).delete(manufacturer1);
    }

    @Test
    void testDeleteManufacturer_NotFound() {
        when(manufacturerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> manufacturerService.deleteManufacturer(99));

    }

}
