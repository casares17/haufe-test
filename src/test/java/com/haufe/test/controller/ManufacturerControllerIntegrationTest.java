package com.haufe.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.test.domain.Manufacturer;
import com.haufe.test.dto.ManufacturerDto;
import com.haufe.test.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ManufacturerControllerIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @BeforeEach
    void setUp() {
        manufacturerRepository.deleteAll();
        Manufacturer manu = Manufacturer.builder()
                .name("Test Manufacturer")
                .country("Spain")
                .build();
        manufacturerRepository.save(manu);
    }

    @Test
    void shouldGetAllManufacturers() throws Exception {

        mockMvc.perform(get("/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Manufacturer"))
                .andExpect(jsonPath("$[0].country").value("Spain"));
    }

    @Test
    void shouldGetManufacturerById() throws Exception {

        Integer savedId = manufacturerRepository.findAll().get(0).getId();

        mockMvc.perform(get("/manufacturers/{id}", savedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Manufacturer"))
                .andExpect(jsonPath("$.country").value("Spain"));
    }

    @Test
    void testCreateManufacturer() throws Exception {
        ManufacturerDto newManufacturer = ManufacturerDto.builder()
                .name("New Manufacturer")
                .country("Portugal")
                .build();

        mockMvc.perform(post("/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newManufacturer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Manufacturer"))
                .andExpect(jsonPath("$.country").value("Portugal"));

        assertThat(manufacturerRepository.count()).isEqualTo(2);
    }

    @Test
    void shouldUpdateManufacturer() throws Exception {
        ManufacturerDto updatedManufacturer = ManufacturerDto.builder()
                .name("Updated Manufacturer")
                .country("Spain")
                .build();

        Integer savedId = manufacturerRepository.findAll().get(0).getId();

        mockMvc.perform(put("/manufacturers/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updatedManufacturer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Manufacturer"))
                .andExpect(jsonPath("$.country").value("Spain"));

        assertThat(manufacturerRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldDeleteManufacturer() throws Exception {

        Integer savedId = manufacturerRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/manufacturers/{id}", savedId))
                .andExpect(status().isNoContent());

        assertThat(manufacturerRepository.findById(savedId)).isEmpty();

    }
}
