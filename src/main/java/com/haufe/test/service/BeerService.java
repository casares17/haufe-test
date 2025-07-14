package com.haufe.test.service;

import com.haufe.test.domain.Beer;
import com.haufe.test.domain.BeerSortField;
import com.haufe.test.domain.mapper.BeerMapper;
import com.haufe.test.dto.BeerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.exception.ServerException;
import com.haufe.test.repository.BeerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeerService {

    private final ManufacturerService manufacturerService;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public List<BeerDto> getAllBeers(String sortBy, String direction) {
        BeerSortField sortField = BeerSortField.from(sortBy)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort field: " + sortBy));

        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.ASC);
        Sort sort = Sort.by(sortDirection, sortField.getField());

        return beerMapper.toDtoList(beerRepository.findAll(sort));
    }

    public BeerDto getBeerById(Integer id) throws NotFoundException {
        return beerMapper.toDto(findBeerById(id));
    }

    public BeerDto createBeer(BeerDto beerDto) throws ServerException {
        if (beerDto.getManufacturerDetails() == null || beerDto.getManufacturerDetails().getId() == null) {
            throw new IllegalArgumentException("Manufacturer ID is required to set the manufacturer for the beer");
        }

        Beer beer = beerMapper.toEntity(beerDto);

        setManufacturer(beerDto, beer);

        return beerMapper.toDto(beerRepository.save(beer));
    }

    public BeerDto updateBeer(Integer id, BeerDto beerDto) throws NotFoundException, ServerException {
        Beer existingBeer = findBeerById(id);
        existingBeer.setName(beerDto.getName());
        existingBeer.setBeerType(beerDto.getBeerType());
        existingBeer.setAlcoholByVolume(beerDto.getAlcoholByVolume());
        existingBeer.setDescription(beerDto.getDescription());

        setManufacturer(beerDto, existingBeer);

        return beerMapper.toDto(beerRepository.save(existingBeer));
    }

    public void deleteBeer(Integer id) throws NotFoundException {
        Beer beer = findBeerById(id);
        beerRepository.delete(beer);
    }

    private Beer findBeerById(Integer id) throws NotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Beer with id %s not found", id)));
    }

    private void setManufacturer(BeerDto beerDto, Beer existingBeer) throws ServerException {
        try {
            existingBeer.setManufacturer(manufacturerService.findManufacturerById(beerDto.getManufacturerDetails().getId()));
        } catch (Exception e) {
            throw new ServerException(e, "Failed to set manufacturer for beer because it does not exist");
        }
    }
}
