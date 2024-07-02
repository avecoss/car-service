package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Category;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CarSpecification;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        Car saved = carRepository.save(modelMapper.map(carDTO, Car.class));
        return modelMapper.map(saved, CarDTO.class);
    }

    @Transactional
    public CarDTO updateCar(CarDTO carDTO) {
        Car existingCar = carRepository.findById(carDTO.getId())
            .map(car -> {
                if (carDTO.getObjectId() != null) car.setObjectId(carDTO.getObjectId());
                car.setYear(carDTO.getYear());
                car.setCarModel(modelMapper.map(carDTO.getCarModel(), CarModel.class));
                Set<Category> categories = carDTO.getCategories().stream()
                    .map(categoryDTO -> modelMapper.map(categoryDTO, Category.class))
                    .collect(Collectors.toSet());
                car.setCategories(categories);

                return car;
            })
            .orElseThrow(() -> createEntityNotExistException(carDTO.getId()));

        Car updated = carRepository.save(existingCar);
        return modelMapper.map(updated, CarDTO.class);
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
    public void deleteCar(Long id) {
        Car existingCar = carRepository.findById(id).orElseThrow(() -> createEntityNotExistException(id));
        carRepository.delete(existingCar);
    }

    private EntityNotExistException createEntityNotExistException(Long id) {
        log.error("Car by id: {} not found", id);
        return new EntityNotExistException("Car not found");
    }
}
