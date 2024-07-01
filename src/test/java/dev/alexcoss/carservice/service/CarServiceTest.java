package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.CategoryDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.request.CarRequestDTO;
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
        CarRequestDTO carRequestDTO = getCarRequestDTO(manufacturer, model, year, carDTO);

        CarModel carModel = new CarModel();
        carModel.setName(model);

        Car car = new Car();
        car.setYear(year);
        car.setCarModel(carModel);

        when(carModelRepository.findByProducerNameAndName(manufacturer, model)).thenReturn(Optional.of(carModel));
        when(modelMapper.map(any(CarModel.class), eq(CarModelDTO.class))).thenReturn(new CarModelDTO());
        when(modelMapper.map(any(CarDTO.class), eq(Car.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(modelMapper.map(any(Car.class), eq(CarDTO.class))).thenReturn(carDTO);

        CarDTO createdCarDTO = carService.createCar(carRequestDTO);

        assertNotNull(createdCarDTO);
        verify(carModelRepository, times(1)).findByProducerNameAndName(manufacturer, model);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(modelMapper, times(3)).map(any(), any());
    }

    @Test
    void testUpdateCar() {
        String year = "2020";
        String manufacturer = "Tesla";
        String model = "Model S";
        CarDTO carDTO = getCarDTO(year, manufacturer, model);
        CarRequestDTO carRequestDTO = getCarRequestDTO(manufacturer, model, year, carDTO);
        CarModelDTO carModelDTO = getCarModelDTO(manufacturer, model);

        Producer producer = Producer.builder()
            .id(1L)
            .name(manufacturer)
            .build();
        CarModel carModel = CarModel.builder()
            .id(1L)
            .name(model)
            .producer(producer)
            .build();
        Car existingCar = Car.builder()
            .id(1L)
            .year(year)
            .carModel(carModel)
            .build();

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setYear("2023");
        updatedCar.setCarModel(carModel);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Sedan");
        Set<CategoryDTO> categoriesDTO = Set.of(categoryDTO);
        carDTO.setCategories(categoriesDTO);

        Category category = new Category();
        category.setId(1L);
        category.setName("Sedan");
        Set<Category> categories = Set.of(category);
        updatedCar.setCategories(categories);

        when(carRepository.findByCarModelNameAndYear(model, year)).thenReturn(Optional.of(existingCar));
        when(modelMapper.map(carModelDTO, CarModel.class)).thenReturn(carModel);
        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(carRepository.save(existingCar)).thenReturn(updatedCar);
        when(modelMapper.map(updatedCar, CarDTO.class)).thenReturn(carDTO);

        CarDTO updatedCarDTO = carService.updateCar(carRequestDTO);

        assertNotNull(updatedCarDTO);
        verify(carRepository, times(1)).findByCarModelNameAndYear(model, year);
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
    void testCreateCarWithModelNotFound() {
        CarRequestDTO carRequestDTO = new CarRequestDTO();
        carRequestDTO.setManufacturer("Unknown");
        carRequestDTO.setModel("UnknownModel");

        when(carModelRepository.findByProducerNameAndName("Unknown", "UnknownModel")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carService.createCar(carRequestDTO));

        verify(carModelRepository, times(1)).findByProducerNameAndName("Unknown", "UnknownModel");
        verify(carRepository, times(0)).save(any(Car.class));
    }

    @Test
    void testUpdateCarWithCarNotFound() {
        CarRequestDTO carRequestDTO = new CarRequestDTO();
        carRequestDTO.setManufacturer("Tesla");
        carRequestDTO.setModel("Model S");
        carRequestDTO.setYear("2022");

        when(carRepository.findByCarModelNameAndYear("Model S", "2022")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> carService.updateCar(carRequestDTO));

        verify(carRepository, times(1)).findByCarModelNameAndYear("Model S", "2022");
        verify(carRepository, times(0)).save(any(Car.class));
    }

    private CarRequestDTO getCarRequestDTO(String manufacturer, String model, String year, CarDTO carDTO) {
        CarRequestDTO carRequestDTO = new CarRequestDTO();
        carRequestDTO.setManufacturer(manufacturer);
        carRequestDTO.setModel(model);
        carRequestDTO.setYear(year);
        carRequestDTO.setCarDTO(carDTO);
        return carRequestDTO;
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
