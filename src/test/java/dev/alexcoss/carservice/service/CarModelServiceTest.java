package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.request.ModelRequestDTO;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
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
    private ProducerRepository producerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarModelService carModelService;


    @Test
    void testCreateCarModelWithValidProducer() {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO();
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("Model S");
        modelRequestDTO.setCarModelDTO(carModelDTO);
        modelRequestDTO.setManufacturer("Tesla");

        Producer producer = new Producer();
        producer.setName("Tesla");

        CarModel carModel = new CarModel();
        carModel.setName("Model S");

        when(producerRepository.findByName("Tesla")).thenReturn(Optional.of(producer));
        when(modelMapper.map(any(Producer.class), any())).thenReturn(new ProducerDTO());
        when(modelMapper.map(any(CarModelDTO.class), any())).thenReturn(carModel);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(modelMapper.map(any(CarModel.class), any())).thenReturn(carModelDTO);

        CarModelDTO createdCarModelDTO = carModelService.createCarModel(modelRequestDTO);

        assertNotNull(createdCarModelDTO);
        verify(producerRepository, times(1)).findByName("Tesla");
        verify(carModelRepository, times(1)).save(any(CarModel.class));
    }

    @Test
    void testUpdateCarModel() {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO();
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("Model 3");
        modelRequestDTO.setCarModelDTO(carModelDTO);
        modelRequestDTO.setManufacturer("Tesla");
        modelRequestDTO.setModel("Model S");

        CarModel existingCarModel = new CarModel();
        existingCarModel.setName("Model S");

        CarModel updatedCarModel = new CarModel();
        updatedCarModel.setName("Model 3");

        when(carModelRepository.findByProducerNameAndName("Tesla", "Model S")).thenReturn(Optional.of(existingCarModel));
        when(carModelRepository.save(any(CarModel.class))).thenReturn(updatedCarModel);
        when(modelMapper.map(any(CarModel.class), any())).thenReturn(carModelDTO);

        CarModelDTO updatedCarModelDTO = carModelService.updateCarModel(modelRequestDTO);

        assertNotNull(updatedCarModelDTO);
        verify(carModelRepository, times(1)).findByProducerNameAndName("Tesla", "Model S");
        verify(carModelRepository, times(1)).save(existingCarModel);
        verify(modelMapper, times(1)).map(any(CarModel.class), any());
    }

    @Test
    void testCreateCarModelProducerNotFound() {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO();
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("Model X");
        modelRequestDTO.setCarModelDTO(carModelDTO);
        modelRequestDTO.setManufacturer("Unknown");

        when(producerRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carModelService.createCarModel(modelRequestDTO));

        verify(producerRepository, times(1)).findByName("Unknown");
        verify(carModelRepository, times(0)).save(any(CarModel.class));
    }

    @Test
    void testUpdateCarModelNotFound() {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO();
        CarModelDTO carModelDTO = new CarModelDTO();
        carModelDTO.setName("Model Y");
        modelRequestDTO.setCarModelDTO(carModelDTO);
        modelRequestDTO.setManufacturer("Tesla");
        modelRequestDTO.setModel("Unknown");

        when(carModelRepository.findByProducerNameAndName("Tesla", "Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carModelService.updateCarModel(modelRequestDTO));

        verify(carModelRepository, times(1)).findByProducerNameAndName("Tesla", "Unknown");
        verify(carModelRepository, times(0)).save(any(CarModel.class));
    }
}