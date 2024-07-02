package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.request.ModelRequestDTO;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalModelException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CarModelServiceTest {

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarModelService carModelService;

    @Test
    void testCreateCarModel() {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("Model S");
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("Producer");
        carModelDTO.setProducer(producerDTO);

        CarModel carModel = new CarModel();
        carModel.setName("Model S");

        when(modelMapper.map(any(CarModelDTO.class), eq(CarModel.class))).thenReturn(carModel);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(modelMapper.map(any(CarModel.class), eq(CarModelDTO.class))).thenReturn(carModelDTO);

        CarModelDTO createdCarModelDTO = carModelService.createCarModel(carModelDTO);

        assertNotNull(createdCarModelDTO);
        assertEquals("Model S", createdCarModelDTO.getName());
        verify(carModelRepository, times(1)).save(any(CarModel.class));
        verify(modelMapper, times(1)).map(any(CarModelDTO.class), eq(CarModel.class));
        verify(modelMapper, times(1)).map(any(CarModel.class), eq(CarModelDTO.class));
    }

    @Test
    void testUpdateCarModel() {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setId(1L);
        carModelDTO.setName("Model 3");
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("Producer");
        carModelDTO.setProducer(producerDTO);

        CarModel existingCarModel = new CarModel();
        existingCarModel.setId(1L);
        existingCarModel.setName("OldName");
        Producer existingProducer = new Producer();
        existingProducer.setId(1L);
        existingProducer.setName("Producer");
        existingCarModel.setProducer(existingProducer);

        CarModel updatedCarModel = new CarModel();
        updatedCarModel.setId(1L);
        updatedCarModel.setName("Model 3");
        updatedCarModel.setProducer(existingProducer);

        when(carModelRepository.findById(carModelDTO.getId())).thenReturn(Optional.of(existingCarModel));
        when(carModelRepository.save(any(CarModel.class))).thenReturn(updatedCarModel);

        doReturn(existingProducer).when(modelMapper).map(producerDTO, Producer.class);
        doReturn(carModelDTO).when(modelMapper).map(updatedCarModel, CarModelDTO.class);

        CarModelDTO updatedCarModelDTO = carModelService.updateCarModel(carModelDTO);

        assertNotNull(updatedCarModelDTO);
        assertEquals("Model 3", updatedCarModelDTO.getName());
        verify(carModelRepository, times(1)).findById(carModelDTO.getId());
        verify(carModelRepository, times(1)).save(existingCarModel);
        verify(modelMapper, times(1)).map(updatedCarModel, CarModelDTO.class);
        verify(modelMapper, times(1)).map(producerDTO, Producer.class);
    }


    @Test
    void testCreateCarModelInvalidCarModel() {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("");

        assertThrows(IllegalModelException.class, () -> carModelService.createCarModel(carModelDTO));

        verify(carModelRepository, times(0)).save(any(CarModel.class));
    }

    @Test
    void testUpdateCarModelNotFound() {
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setId(1L);
        carModelDTO.setName("Model Y");
        ProducerDTO producerDTO = new ProducerDTO();
        producerDTO.setId(1L);
        producerDTO.setName("Producer");
        carModelDTO.setProducer(producerDTO);

        when(carModelRepository.findById(carModelDTO.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carModelService.updateCarModel(carModelDTO));

        verify(carModelRepository, times(1)).findById(carModelDTO.getId());
        verify(carModelRepository, times(0)).save(any(CarModel.class));
    }
}