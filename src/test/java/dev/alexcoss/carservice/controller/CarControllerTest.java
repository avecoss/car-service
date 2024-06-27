package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

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
            .id("testId")
            .year("2020")
            .carModel(carModelDTO)
            .categories(Set.of())
            .build();
    }

    @Test
    @WithMockUser
    public void testCreateCar() throws Exception {
        when(carService.createCar(anyString(), anyString(), anyString(), any(CarDTO.class))).thenReturn(carDTO);

        mockMvc.perform(post("/api/v1/manufacturers/Toyota/models/Corolla/2021")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"testId\",\"manufacturer\":\"Audi\",\"model\":\"Q3\",\"year\":\"2020\",\"category\":\"[]\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.manufacturer").value("Audi"))
            .andExpect(jsonPath("$.model").value("Q3"));
    }
}