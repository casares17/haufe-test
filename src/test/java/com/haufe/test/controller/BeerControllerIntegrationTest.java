package com.haufe.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.test.domain.Beer;
import com.haufe.test.domain.BeerType;
import com.haufe.test.domain.Manufacturer;
import com.haufe.test.dto.BeerDto;
import com.haufe.test.repository.BeerRepository;
import com.haufe.test.repository.ManufacturerRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BeerControllerIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "adminPass";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @BeforeEach
    void setupData() {
        manufacturerRepository.deleteAll();
        beerRepository.deleteAll();

        Manufacturer manu = Manufacturer.builder()
                .name("Test Manufacturer")
                .country("Spain")
                .build();
        manufacturerRepository.save(manu);

        Beer beer1 = Beer.builder()
                .name("Beer 1")
                .alcoholByVolume(5.5)
                .beerType(BeerType.IPA)
                .description("Test beer description")
                .manufacturer(manu)
                .build();
        beerRepository.save(beer1);

        Beer beer2 = Beer.builder()
                .name("Beer 2")
                .alcoholByVolume(3.5)
                .beerType(BeerType.LAGGER)
                .description("Test beer description")
                .manufacturer(manu)
                .build();
        beerRepository.save(beer2);
    }

    @Test
    void testGetAllBeers() throws Exception {
        mockMvc.perform(get("/beers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Beer 1"))
                .andExpect(jsonPath("$[0].manufacturerDetails.name").value("Test Manufacturer"));
    }

    @Test
    void testCreateBeer() throws Exception {
        Integer manufacturerId = manufacturerRepository.findAll().get(0).getId();
        BeerDto newBeerDto = BeerDto.builder()
                .name("New Beer")
                .alcoholByVolume(4.2)
                .beerType(BeerType.LAGGER)
                .description("A refreshing lager")
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(manufacturerId).build())
                .build();

        mockMvc.perform(post("/beers")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newBeerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Beer"))
                .andExpect(jsonPath("$.beerType").value("LAGGER"))
                .andExpect(jsonPath("$.alcoholByVolume").value("4.2"))
                .andExpect(jsonPath("$.manufacturerDetails.name").value("Test Manufacturer"));

        // Verify repository size increased
        assertThat(beerRepository.count()).isEqualTo(3);
    }

    @Test
    void testCreateBeer_Error() throws Exception {
        BeerDto newBeerDto = BeerDto.builder()
                .name("New Beer")
                .alcoholByVolume(4.2)
                .beerType(BeerType.LAGGER)
                .description("A refreshing lager")
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(99).build())
                .build();

        mockMvc.perform(post("/beers")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newBeerDto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testCreateBeer_Unauthorized() throws Exception {
        BeerDto newBeerDto = BeerDto.builder()
                .name("New Beer")
                .alcoholByVolume(4.2)
                .beerType(BeerType.LAGGER)
                .description("A refreshing lager")
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(99).build())
                .build();

        mockMvc.perform(post("/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newBeerDto)))
                .andExpect(status().is(401));
    }

    @Test
    void testCreateBeer_Forbidden() throws Exception {
        BeerDto newBeerDto = BeerDto.builder()
                .name("New Beer")
                .alcoholByVolume(4.2)
                .beerType(BeerType.LAGGER)
                .description("A refreshing lager")
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder().id(99).build())
                .build();

        mockMvc.perform(post("/beers")
                        .with(httpBasic("manufacturer", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newBeerDto)))
                .andExpect(status().is(403));
    }

    @Test
    void testUpdateBeer() throws Exception {
        Beer existingBeer = beerRepository.findAll().get(0);
        Integer beerId = existingBeer.getId();
        BeerDto updateDto = BeerDto.builder()
                .name("Updated Beer")
                .alcoholByVolume(6.0)
                .beerType(BeerType.IPA)
                .description("Updated description")
                .manufacturerDetails(BeerDto.ManufacturerDetails.builder()
                        .id(existingBeer.getManufacturer().getId())
                        .build())
                .build();

        mockMvc.perform(put("/beers/{id}", beerId)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Beer"))
                .andExpect(jsonPath("$.alcoholByVolume").value("6.0"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testUpdateBeer_NotFound() throws Exception {

        mockMvc.perform(put("/beers/9999")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(new BeerDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBeer() throws Exception {
        Integer beerId = beerRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/beers/{id}", beerId)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isNoContent());

        assertThat(beerRepository.findById(beerId)).isEmpty();
    }

    @Test
    void testDeleteBeer_NotFound() throws Exception {
        mockMvc.perform(delete("/beers/9999")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "Searching with: {0} should return {1}")
    @MethodSource("search")
    void testSearchBeers(BeerSearchRequest searchRequest, List<BeerDto> results) throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/beers/search")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(searchRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        if (!results.isEmpty()) {
            resultActions
                    .andExpect(jsonPath("$.content.length()").value(results.size()))
                    .andExpect(jsonPath("$.content[0].name").value(results.getFirst().getName()))
                    .andExpect(jsonPath("$.content[0].beerType").value(results.getFirst().getBeerType().toString()))
                    .andExpect(jsonPath("$.content[0].alcoholByVolume").value(results.getFirst().getAlcoholByVolume()))
                    .andExpect(jsonPath("$.content[0].manufacturerDetails.name").value(
                            results.getFirst().getManufacturerDetails().getName()));
        } else {
            resultActions.andExpect(jsonPath("$.content.length()").value(0));
        }

    }


    @Test
    void testSearchBeers_Error() throws Exception {

        BeerSearchRequest searchRequest = new BeerSearchRequest();
        searchRequest.setSortBy("invalidField");

        mockMvc.perform(post("/beers/search")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(searchRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    public static Stream<Arguments> search() {

        BeerDto.ManufacturerDetails manu = BeerDto.ManufacturerDetails.builder()
                .name("Test Manufacturer")
                .country("Spain")
                .build();

        BeerDto beerDto1 = BeerDto.builder()
                .name("Beer 1")
                .alcoholByVolume(5.5)
                .beerType(BeerType.IPA)
                .description("Test beer description")
                .manufacturerDetails(manu)
                .build();

        BeerDto beerDto2 = BeerDto.builder()
                .name("Beer 2")
                .alcoholByVolume(3.5)
                .beerType(BeerType.LAGGER)
                .description("Test beer description")
                .manufacturerDetails(manu)
                .build();

        return Stream.of(
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Beer 1").build(),
                        List.of(beerDto1))
                ,
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Beer 2").build(),
                        List.of(beerDto2)),
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Beer").build(),
                        List.of(beerDto1, beerDto2)),
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Beer").sortBy("name").direction("desc").build(),
                        List.of(beerDto2, beerDto1)),
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Water").build(),
                        List.of()),
                Arguments.arguments(
                        BeerSearchRequest.builder().manufacturerName("Test Manufacturer").build(),
                        List.of(beerDto1, beerDto2)),
                Arguments.arguments(
                        BeerSearchRequest.builder().minAbv(0.5).build(),
                        List.of(beerDto1, beerDto2)),
                Arguments.arguments(
                        BeerSearchRequest.builder().minAbv(4.5).build(),
                        List.of(beerDto1)),
                Arguments.arguments(
                        BeerSearchRequest.builder().maxAbv(4.5).build(),
                        List.of(beerDto2)),
                Arguments.arguments(
                        BeerSearchRequest.builder().minAbv(6.5).maxAbv(9.0).build(),
                        List.of()),
                Arguments.arguments(
                        BeerSearchRequest.builder().beerType(BeerType.IPA).build(),
                        List.of(beerDto1)),
                Arguments.arguments(
                        BeerSearchRequest.builder().name("Beer").page(0).size(1)
                                .build(),
                        List.of(beerDto1)),
                Arguments.arguments(
                        BeerSearchRequest.builder().sortBy("alcoholByVolume").direction("asc").build(),
                        List.of(beerDto2, beerDto1))
        );
    }
}
