package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.CategoryDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Category;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarService carService;

    @Test
    void testCreateCar() {
        String year = "2020";
        String manufacturer = "Tesla";
        String model = "Model S";
        CarDTO carDTO = getCarDTO(year, manufacturer, model);

        CarModel carModel = new CarModel();
        carModel.setName(model);

        Car car = new Car();
        car.setYear(year);
        car.setCarModel(carModel);

        when(modelMapper.map(any(CarDTO.class), eq(Car.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(modelMapper.map(any(Car.class), eq(CarDTO.class))).thenReturn(carDTO);

        CarDTO createdCarDTO = carService.createCar(carDTO);

        assertNotNull(createdCarDTO);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    void testUpdateCar() {
        String year = "2020";
        String manufacturer = "Tesla";
        String model = "Model S";
        CarDTO carDTO = getCarDTO(year, manufacturer, model);
        Car existingCar = new Car();
        CarModel carModel = new CarModel();
        carModel.setName(carDTO.getCarModel().getName());
        existingCar.setCarModel(carModel);

        when(carRepository.findById(carDTO.getId())).thenReturn(Optional.of(existingCar));
        when(modelMapper.map(carDTO.getCarModel(), CarModel.class)).thenReturn(carModel);
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);
        when(modelMapper.map(any(Car.class), eq(CarDTO.class))).thenReturn(carDTO);

        CarDTO updatedCarDTO = carService.updateCar(carDTO);

        assertNotNull(updatedCarDTO);
        verify(carRepository, times(1)).findById(carDTO.getId());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testGetListCarsWithPagination() {
        String producerName = "Tesla";
        String modelName = "Model S";
        Integer minYear = 2020;
        Integer maxYear = 2023;
        String category = "Sedan";
        Pageable pageable = PageRequest.of(0, 10);

        Car car = new Car();
        car.setYear("2022");
        CarModel carModel = new CarModel();
        carModel.setName("Model S");
        car.setCarModel(carModel);
        Page<Car> carPage = new PageImpl<>(Collections.singletonList(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(carPage);
        when(modelMapper.map(any(Car.class), eq(CarDTO.class))).thenReturn(new CarDTO());

        Page<CarDTO> result = carService.getListCarsWithPagination(producerName, modelName, minYear, maxYear, category, pageable);

        verify(carRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(modelMapper, times(1)).map(any(Car.class), eq(CarDTO.class));
    }

    @Test
    void testUpdateCarWithCarNotFound() {
        CarDTO carDTO = new CarDTO();

        when(carRepository.findById(carDTO.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carService.updateCar(carDTO));

        verify(carRepository, times(1)).findById(carDTO.getId());
        verify(carRepository, times(0)).save(any(Car.class));
    }

    private CarDTO getCarDTO(String year, String producer, String model) {
        return CarDTO.builder()
            .id(1L)
            .year(year)
            .carModel(getCarModelDTO(producer, model))
            .build();
    }

    private CarModelDTO getCarModelDTO(String producer, String model) {
        ProducerDTO producerDTO = ProducerDTO.builder()
            .id(1L)
            .name(producer)
            .build();
        return CarModelDTO.builder()
            .id(1L)
            .name(model)
            .producer(producerDTO)
            .build();
    }
}
