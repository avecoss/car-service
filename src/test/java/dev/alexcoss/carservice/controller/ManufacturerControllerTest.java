package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.request.ModelRequestDTO;
import dev.alexcoss.carservice.service.CarModelService;
import dev.alexcoss.carservice.service.ProducerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ManufacturerController.class)
@AutoConfigureMockMvc
class ManufacturerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProducerService producerService;

    @MockBean
    private CarModelService carModelService;

    @Test
    @WithMockUser
    void testCreateProducer() throws Exception {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("TestProducer");
        when(producerService.createProducer(any(ProducerDTO.class))).thenReturn(producerDTO);

        mockMvc.perform(post("/api/v1/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestProducer\"}")
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/v1/manufacturers/TestProducer"))
            .andExpect(jsonPath("$.name").value("TestProducer"));
    }

    @Test
    @WithMockUser
    void testUpdateProducer() throws Exception {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("UpdatedProducer");
        Mockito.when(producerService.updateProducer(anyString(), any(ProducerDTO.class))).thenReturn(producerDTO);

        mockMvc.perform(patch("/api/v1/manufacturers/TestProducer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"UpdatedProducer\"}")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedProducer"));
    }

    @Test
    @WithMockUser
    void testListOfProducers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProducerDTO> page = new PageImpl<>(Collections.singletonList(new ProducerDTO()));
        Mockito.when(producerService.getListOfProducers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/manufacturers")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void testDeleteProducer() throws Exception {
        Mockito.doNothing().when(producerService).deleteProducer(anyString());

        mockMvc.perform(delete("/api/v1/manufacturers/TestProducer").with(csrf()))
            .andExpect(status().isNoContent());
    }

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
        Mockito.when(carModelService.createCarModel(any(ModelRequestDTO.class))).thenReturn(carModelDTO);

        mockMvc.perform(post("/api/v1/manufacturers/TestManufacturer/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"model\": \"TestModel\"}")
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/v1/manufacturers/TestManufacturer/models"))
            .andExpect(jsonPath("$.name").value(carModelDTO.getName()));
    }

    @Test
    @WithMockUser
    void testUpdateCarModel() throws Exception {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("UpdatedModel");
        Mockito.when(carModelService.updateCarModel(any(ModelRequestDTO.class))).thenReturn(carModelDTO);

        mockMvc.perform(patch("/api/v1/manufacturers/TestManufacturer/models/TestModel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"model\": \"UpdatedModel\"}")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedModel"));
    }

    @Test
    @WithMockUser
    void testListCarModels() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CarModelDTO> page = new PageImpl<>(Collections.singletonList(new CarModelDTO()));
        Mockito.when(carModelService.getListCarModels(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/manufacturers/TestManufacturer/models")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void testDeleteCarModel() throws Exception {
        Mockito.doNothing().when(carModelService).deleteCarModel(any(ModelRequestDTO.class));

        mockMvc.perform(delete("/api/v1/manufacturers/TestManufacturer/models/TestModel").with(csrf()))
            .andExpect(status().isNoContent());
    }

}