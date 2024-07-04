package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ModelLinkHelper;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.CarModelService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarModelController.class)
@AutoConfigureMockMvc
class CarModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarModelService carModelService;

    @MockBean
    private ModelLinkHelper linkHelper;

    @Test
    @WithMockUser
    void testCreateCarModel() throws Exception {
        CarModelDTO carModelDTO = CarModelDTO.builder()
            .id(1L)
            .name("TestModel")
            .producer(ProducerDTO.builder()
                .id(1L)
                .name("TestManufacturer")
                .build())
            .build();

        when(carModelService.createCarModel(any(CarModelDTO.class))).thenReturn(carModelDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createModelsLink()).thenReturn(Link.of("modelsLink"));

        mockMvc.perform(post("/api/v1/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"TestModel\", \"producer\": {\"id\": 1,\"name\":\"TestManufacturer\"}}")
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/v1/models/1"))
            .andExpect(jsonPath("$.name").value(carModelDTO.getName()))
            .andExpect(jsonPath("$.producer").value(carModelDTO.getProducer()));
    }

    @Test
    @WithMockUser
    void testUpdateCarModel() throws Exception {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setId(1L);
        carModelDTO.setName("UpdatedModel");

        when(carModelService.updateCarModel(any(CarModelDTO.class))).thenReturn(carModelDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createModelsLink()).thenReturn(Link.of("modelsLink"));

        mockMvc.perform(patch("/api/v1/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"UpdatedModel\", \"producer\": {\"id\": 1,\"name\":\"TestManufacturer\"}}")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedModel"));
    }

    @Test
    @WithMockUser
    void testListCarModels() throws Exception {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setId(1L);
        carModelDTO.setName("UpdatedModel");
        Page<CarModelDTO> page = new PageImpl<>(Collections.singletonList(carModelDTO));

        when(carModelService.getListCarModels(anyString(), any(Pageable.class))).thenReturn(page);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createModelsLink(anyString(), any(Pageable.class))).thenReturn(Link.of("modelsLink"));

        mockMvc.perform(get("/api/v1/models?manufacturer=TestModel")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void testDeleteCarModel() throws Exception {
        Mockito.doNothing().when(carModelService).deleteCarModel(anyLong());

        mockMvc.perform(delete("/api/v1/models/1").with(csrf()))
            .andExpect(status().isNoContent());
    }
}