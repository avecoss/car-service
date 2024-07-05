package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.CarsLinkHelper;
import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @MockBean
    private CarsLinkHelper linkHelper;

    private CarDTO carDTO;

    @BeforeEach
    public void setUp() {
        ProducerDTO producerDTO = ProducerDTO.builder()
            .id(1L)
            .name("Audi")
            .build();
        CarModelDTO carModelDTO = CarModelDTO.builder()
            .id(1L)
            .name("Q3")
            .producer(producerDTO)
            .build();

        carDTO = CarDTO.builder()
            .id(1L)
            .objectId("testId")
            .year("2020")
            .carModel(carModelDTO)
            .categories(Set.of())
            .build();
    }

    @Test
    @WithMockUser
    public void testCreateCar() throws Exception {
        when(carService.createCar(any(CarDTO.class))).thenReturn(carDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createCarsLink()).thenReturn(Link.of("carsLink"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"objectId\":\"testId\",\"year\":\"2020\",\"carModel\":{\"id\":1,\"name\":\"Q3\",\"producer\":{\"id\":1,\"name\":\"Audi\"}},\"categories\":[]}")
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/v1/cars/1"))
            .andExpect(jsonPath("$.objectId").value("testId"))
            .andExpect(jsonPath("$.year").value("2020"))
            .andExpect(jsonPath("$.carModel.name").value("Q3"))
            .andExpect(jsonPath("$.carModel.producer.name").value("Audi"))
            .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    @WithMockUser
    void testListCars() throws Exception {
        Page<CarDTO> page = new PageImpl<>(Collections.singletonList(carDTO));
        when(carService.getListCarsWithPagination(any())).thenReturn(page);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createCarsLink(any())).thenReturn(Link.of("carsLink"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars")
                .param("manufacturer", "Audi")
                .param("model", "Q3")
                .param("minYear", "2020")
                .param("category", "Sedan")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].objectId").value("testId"))
            .andExpect(jsonPath("$.content[0].year").value("2020"))
            .andExpect(jsonPath("$.content[0].carModel.producer.name").value("Audi"))
            .andExpect(jsonPath("$.content[0].carModel.name").value("Q3"))
            .andExpect(jsonPath("$.content[0].categories").isEmpty());
    }

    @Test
    @WithMockUser
    void testUpdateCar() throws Exception {
        when(carService.updateCar(any(CarDTO.class))).thenReturn(carDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createCarsLink()).thenReturn(Link.of("carsLink"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"objectId\":\"testId\",\"year\":\"2020\",\"carModel\":{\"id\":1,\"name\":\"Q3\",\"producer\":{\"id\":1,\"name\":\"Audi\"}},\"categories\":[]}")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.objectId").value("testId"))
            .andExpect(jsonPath("$.year").value("2020"))
            .andExpect(jsonPath("$.carModel.name").value("Q3"))
            .andExpect(jsonPath("$.carModel.producer.name").value("Audi"))
            .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    @WithMockUser
    void testDeleteCar() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1").with(csrf()))
            .andExpect(status().isNoContent());
    }
}