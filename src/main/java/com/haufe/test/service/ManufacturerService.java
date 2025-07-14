package com.haufe.test.service;

import com.haufe.test.domain.Manufacturer;
import com.haufe.test.domain.mapper.ManufacturerMapper;
import com.haufe.test.dto.ManufacturerDto;
import com.haufe.test.exception.NotFoundException;
import com.haufe.test.repository.ManufacturerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public List<ManufacturerDto> getAllManufacturers() {
        return manufacturerMapper.toDtoList(manufacturerRepository.findAll());
    }

    public ManufacturerDto getManufacturerById(Integer id) throws NotFoundException {
        return manufacturerMapper.toDto(findManufacturerById(id));
    }

    public ManufacturerDto createManufacturer(ManufacturerDto manufacturerDto) {
        Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDto);
        return manufacturerMapper.toDto(manufacturerRepository.save(manufacturer));
    }

    public ManufacturerDto updateManufacturer(Integer id, ManufacturerDto manufacturerDto) throws NotFoundException {
        Manufacturer existingManufacturer = findManufacturerById(id);
        existingManufacturer.setName(manufacturerDto.getName());
        existingManufacturer.setCountry(manufacturerDto.getCountry());
        return manufacturerMapper.toDto(manufacturerRepository.save(existingManufacturer));
    }

    public void deleteManufacturer(Integer id) throws NotFoundException {
        Manufacturer manufacturer = findManufacturerById(id);
        manufacturerRepository.delete(manufacturer);
    }

    protected Manufacturer findManufacturerById(Integer id) throws NotFoundException {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Manufacturer with id %s not found", id)));
    }
}
