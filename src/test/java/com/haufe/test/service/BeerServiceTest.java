package com.haufe.test.service;

import com.haufe.test.domain.Beer;
import com.haufe.test.domain.BeerType;
import com.haufe.test.domain.Manufacturer;
import com.haufe.test.domain.mapper.BeerMapper;
import com.haufe.test.dto.BeerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.exception.ServerException;
import com.haufe.test.repository.BeerRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class BeerServiceTest {

    @Mock
    private ManufacturerService manufacturerService;
    @Mock
    private BeerRepository beerRepository;

    private BeerService beerService;

    private final BeerMapper beerMapper = Mappers.getMapper(BeerMapper.class);

    private Beer beer1;
    private Beer beer2;
    private Manufacturer manufacturer;

    @BeforeEach
    void setUp() {
        manufacturer = Manufacturer.builder().id(1).name("Manufacturer 1").build();

        beer1 = Beer.builder()
                .id(1)
                .name("Test Beer 1")
                .alcoholByVolume(5.0)
                .manufacturer(manufacturer)
                .beerType(BeerType.IPA)
                .build();

        beer2 = Beer.builder()
                .id(2)
                .name("Test Beer 2")
                .alcoholByVolume(4.0)
                .manufacturer(manufacturer)
                .beerType(BeerType.LAGGER)
                .build();


        beerService = new BeerService(manufacturerService, beerRepository, beerMapper);
    }

    @Test
    void testGetAllBeers() {
        List<Beer> beers = List.of(beer1, beer2);

        when(beerRepository.findAll(any(Sort.class))).thenReturn(beers);

        List<BeerDto> result = beerService.getAllBeers("name", "asc");

        assertEquals(2, result.size());
        verify(beerRepository).findAll(Sort.by(Sort.Direction.ASC, "name"));
    }


    @Test
    void testGetBeerById() throws NotFoundException {
        when(beerRepository.findById(1)).thenReturn(Optional.of(beer1));

        BeerDto result = beerService.getBeerById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(beerRepository).findById(1);
    }

    @Test
    void testGetBeerById_NotFound() {
        when(beerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> beerService.getBeerById(1));
    }

    @Test
    void testCreateBeer() throws ServerException, NotFoundException {

        var beerDto = BeerDto.builder()
                .name("Test Beer 1")
                .alcoholByVolume(5.0)
                .beerType(BeerType.IPA)
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(1).build())
                .build();

        when(manufacturerService.findManufacturerById(1)).thenReturn(manufacturer);
        when(beerRepository.save(any())).thenReturn(beer1);


        BeerDto result = beerService.createBeer(beerDto);

        verify(manufacturerService).findManufacturerById(1);
        verify(beerRepository).save(any());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Beer 1");
        assertThat(result.getAlcoholByVolume()).isEqualTo(5.0);
        assertThat(result.getBeerType()).isEqualTo(BeerType.IPA);
        assertThat(result.getManufacturerDetails()).isNotNull();
        assertThat(result.getManufacturerDetails().getId()).isEqualTo(1);
        assertThat(result.getManufacturerDetails().getName()).isEqualTo("Manufacturer 1");
    }

    @Test
    void testCreateBeer_ManufacturerNotProvided() {
        var beerDto = BeerDto.builder()
                .name("Test Beer 1")
                .alcoholByVolume(5.0)
                .beerType(BeerType.IPA)
                .build();
        assertThrows(IllegalArgumentException.class, () -> beerService.createBeer(beerDto));
    }

    @Test
    void testCreateBeer_ManufacturerNotFound() throws NotFoundException {
        when(manufacturerService.findManufacturerById(99)).thenThrow(new RuntimeException());
        var beerDto = BeerDto.builder()
                .name("Test Beer 1")
                .alcoholByVolume(5.0)
                .beerType(BeerType.IPA)
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(99).build())
                .build();
        assertThrows(ServerException.class, () -> beerService.createBeer(beerDto));
    }

    @Test
    void testUpdateBeer() throws NotFoundException, ServerException {
        when(beerRepository.findById(1)).thenReturn(Optional.of(beer1));
        when(manufacturerService.findManufacturerById(1)).thenReturn(manufacturer);
        when(beerRepository.save(any())).thenReturn(beer1);

        var beerDto = BeerDto.builder()
                .id(1)
                .name("Test Beer 1")
                .alcoholByVolume(4.0)
                .beerType(BeerType.STOUT)
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(1).build())
                .build();

        BeerDto result = beerService.updateBeer(1, beerDto);

        assertNotNull(result);
        verify(beerRepository).findById(1);
        verify(manufacturerService).findManufacturerById(1);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Beer 1");
        assertThat(result.getAlcoholByVolume()).isEqualTo(4.0);
        assertThat(result.getBeerType()).isEqualTo(BeerType.STOUT);
        assertThat(result.getManufacturerDetails()).isNotNull();
        assertThat(result.getManufacturerDetails().getId()).isEqualTo(1);
        assertThat(result.getManufacturerDetails().getName()).isEqualTo("Manufacturer 1");
    }

    @Test
    void testUpdateBeer_NotFound() {
        when(beerRepository.findById(99)).thenReturn(Optional.empty());

        var beerDto = BeerDto.builder()
                .id(1)
                .name("Test Beer 1")
                .alcoholByVolume(4.0)
                .beerType(BeerType.STOUT)
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(1).build())
                .build();

        assertThrows(NotFoundException.class, () -> beerService.updateBeer(99, beerDto));
    }

    @Test
    void testDeleteBeer() throws NotFoundException {
        when(beerRepository.findById(1)).thenReturn(Optional.of(beer1));

        beerService.deleteBeer(1);

        verify(beerRepository).findById(1);
        verify(beerRepository).delete(beer1);

    }

    @Test
    void testDeleteBeer_NotFound() {
        when(beerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> beerService.deleteBeer(99));
    }
}

