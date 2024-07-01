package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityAlreadyExistsException;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalProducerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProducerService producerService;

    @Test
    void testCreateProducer() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("Tesla");

        Producer producer = new Producer();
        producer.setName("Tesla");

        when(producerRepository.findByName("Tesla")).thenReturn(Optional.empty());
        when(modelMapper.map(any(ProducerDTO.class), eq(Producer.class))).thenReturn(producer);
        when(producerRepository.save(any(Producer.class))).thenReturn(producer);
        when(modelMapper.map(any(Producer.class), eq(ProducerDTO.class))).thenReturn(producerDTO);

        ProducerDTO createdProducer = producerService.createProducer(producerDTO);

        assertNotNull(createdProducer);
        assertEquals("Tesla", createdProducer.getName());
        verify(producerRepository, times(1)).findByName("Tesla");
        verify(producerRepository, times(1)).save(producer);
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    void testCreateProducerAlreadyExists() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("Tesla");

        Producer producer = new Producer();
        producer.setName("Tesla");

        when(producerRepository.findByName("Tesla")).thenReturn(Optional.of(producer));

        assertThrows(EntityAlreadyExistsException.class, () -> producerService.createProducer(producerDTO));

        verify(producerRepository, times(1)).findByName("Tesla");
        verify(producerRepository, times(0)).save(any(Producer.class));
    }

    @Test
    void testUpdateProducer() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("Tesla");

        Producer existingProducer = new Producer();
        existingProducer.setName("OldName");

        Producer updatedProducer = new Producer();
        updatedProducer.setName("Tesla");

        when(producerRepository.findByName("OldName")).thenReturn(Optional.of(existingProducer));
        when(producerRepository.save(existingProducer)).thenReturn(updatedProducer);
        when(modelMapper.map(any(Producer.class), eq(ProducerDTO.class))).thenReturn(producerDTO);

        ProducerDTO updatedProducerDTO = producerService.updateProducer("OldName", producerDTO);

        assertNotNull(updatedProducerDTO);
        assertEquals("Tesla", updatedProducerDTO.getName());
        verify(producerRepository, times(1)).findByName("OldName");
        verify(producerRepository, times(1)).save(existingProducer);
        verify(modelMapper, times(1)).map(updatedProducer, ProducerDTO.class);
    }

    @Test
    void testUpdateProducerNotExist() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("Tesla");

        when(producerRepository.findByName("OldName")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> producerService.updateProducer("OldName", producerDTO));

        verify(producerRepository, times(1)).findByName("OldName");
        verify(producerRepository, times(0)).save(any(Producer.class));
    }

    @Test
    void testCreateProducerInvalidProducer() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("");

        assertThrows(IllegalProducerException.class, () -> producerService.createProducer(producerDTO));

        verify(producerRepository, times(0)).findByName(anyString());
        verify(producerRepository, times(0)).save(any(Producer.class));
    }

    @Test
    void testUpdateProducerInvalidProducer() {
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setName("");

        assertThrows(IllegalProducerException.class, () -> producerService.updateProducer("OldName", producerDTO));

        verify(producerRepository, times(0)).findByName(anyString());
        verify(producerRepository, times(0)).save(any(Producer.class));
    }
}