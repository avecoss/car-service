package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Category;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CarSpecification;
import dev.alexcoss.carservice.util.exception.DuplicateIdException;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalEntityException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CarDTO createCar(String producerName, String modelName, String year, CarDTO car) {
        isValidCar(car);
        if (carRepository.existsById(car.getId()))
            throw new DuplicateIdException("Duplicate ID: " + car.getId());

        CarModel carModel = carModelRepository.findByProducerNameAndName(producerName, modelName)
            .orElseThrow(() -> new EntityNotExistException("Car model: " + modelName + " not found"));
        car.setCarModel(modelMapper.map(carModel, CarModelDTO.class));
        car.setYear(year);

        Car saved = carRepository.save(modelMapper.map(car, Car.class));
        return modelMapper.map(saved, CarDTO.class);
    }

    @Transactional
    public CarDTO updateCar(String producerName, String modelName, String year, CarDTO car) {
        isValidCar(car);

        Car existingCar = carRepository.findByCarModelNameAndYear(modelName, year)
            .orElseThrow(() -> new EntityNotExistException("Car not found"));

        if (!existingCar.getCarModel().getProducer().getName().equals(producerName))
            throw new IllegalEntityException("Car model producer does not match existing car model");

        existingCar.setYear(car.getYear());
        existingCar.setCarModel(modelMapper.map(car.getCarModel(), CarModel.class));

        Set<Category> categories = car.getCategories().stream()
            .map(categoryDTO -> modelMapper.map(categoryDTO, Category.class))
            .collect(Collectors.toSet());
        existingCar.setCategories(categories);

        Car saved = carRepository.save(modelMapper.map(existingCar, Car.class));
        return modelMapper.map(saved, CarDTO.class);
    }

    public Page<CarDTO> getListCarsWithPagination(String producerName, String modelName, Integer minYear, Integer maxYear,
                                                  String category, Pageable pageable) {
        Specification<Car> spec = Specification.where(CarSpecification.hasProducer(producerName)
                .and(CarSpecification.hasModel(modelName))
                .and(CarSpecification.hasYearGreaterThanOrEqualTo(minYear))
                .and(CarSpecification.hasYearLessThanOrEqualTo(maxYear))
                .and(CarSpecification.hasCategory(category)));

        Page<Car> carsByFilter = carRepository.findAll(spec, pageable);

        return carsByFilter.map(car -> modelMapper.map(car, CarDTO.class));
    }

    @Transactional
    public void deleteCar(String id) {
        Car car = carRepository.findById(id)
            .orElseThrow(() -> new EntityNotExistException("Car not found"));
        carRepository.delete(car);
    }

    private void isValidCar(CarDTO car) {
        if (car.getId() == null || car.getId().isBlank()) {
            throw new IllegalEntityException("Invalid car: " + car.getId());
        }
    }
}
