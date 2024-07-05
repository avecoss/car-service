package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ManufacturerLinkHelper;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.ProducerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ManufacturerLinkHelper linkHelper;

    @Test
    @WithMockUser
    void testCreateProducer() throws Exception {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("TestProducer");

        when(producerService.createProducer(any(ProducerDTO.class))).thenReturn(producerDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createManufacturersLink()).thenReturn(Link.of("manufacturersLink"));

        mockMvc.perform(post("/api/v1/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"TestProducer\"}")
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/v1/manufacturers/1"))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("TestProducer"));
    }

    @Test
    @WithMockUser
    void testUpdateProducer() throws Exception {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("UpdatedProducer");

        when(producerService.updateProducer(any(ProducerDTO.class))).thenReturn(producerDTO);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createManufacturersLink()).thenReturn(Link.of("manufacturersLink"));

        mockMvc.perform(patch("/api/v1/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"UpdatedProducer\"}")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedProducer"));
    }

    @Test
    @WithMockUser
    void testListOfProducers() throws Exception {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("UpdatedProducer");
        Page<ProducerDTO> page = new PageImpl<>(Collections.singletonList(producerDTO));

        when(producerService.getListOfProducers(any(Pageable.class))).thenReturn(page);
        when(linkHelper.createSelfLink(anyLong())).thenReturn(Link.of("selfLink"));
        when(linkHelper.createManufacturersLink(any(Pageable.class))).thenReturn(Link.of("manufacturersLink"));

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
        Mockito.doNothing().when(producerService).deleteProducer(anyLong());

        mockMvc.perform(delete("/api/v1/manufacturers/1").with(csrf()))
            .andExpect(status().isNoContent());
    }
}