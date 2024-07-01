package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.request.CarRequestDTO;
import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Category;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CarSpecification;
import dev.alexcoss.carservice.util.exception.CarModelProducerMismatchException;
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
    public CarDTO createCar(CarRequestDTO carRequestDTO) {
        CarModel carModel = carModelRepository.findByProducerNameAndName(carRequestDTO.getManufacturer(), carRequestDTO.getModel())
            .orElseThrow(() -> {
                log.error("Car model name {} not found", carRequestDTO.getModel());
                return new EntityNotExistException("Car model: " + carRequestDTO.getModel() + " not found");
            });
        carRequestDTO.getCarDTO().setCarModel(modelMapper.map(carModel, CarModelDTO.class));
        carRequestDTO.getCarDTO().setYear(carRequestDTO.getYear());

        Car saved = carRepository.save(modelMapper.map(carRequestDTO.getCarDTO(), Car.class));
        return modelMapper.map(saved, CarDTO.class);
    }

    @Transactional
    public CarDTO updateCar(CarRequestDTO carRequestDTO) {
        Car existingCar = getExistingCar(carRequestDTO);
        existingCar.setYear(carRequestDTO.getCarDTO().getYear());
        existingCar.setCarModel(modelMapper.map(carRequestDTO.getCarDTO().getCarModel(), CarModel.class));

        Set<Category> categories = carRequestDTO.getCarDTO().getCategories().stream()
            .map(categoryDTO -> modelMapper.map(categoryDTO, Category.class))
            .collect(Collectors.toSet());
        existingCar.setCategories(categories);

        Car updated = carRepository.save(existingCar); //todo check
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
    public void deleteCar(CarRequestDTO carRequestDTO) {
        Car existingCar = getExistingCar(carRequestDTO);
        carRepository.delete(existingCar);
    }

    private Car getExistingCar(CarRequestDTO carRequestDTO) {
        Car existingCar = carRepository.findByCarModelNameAndYear(carRequestDTO.getModel(), carRequestDTO.getYear())
            .orElseThrow(() -> {
                log.error("Car not found");
               return new EntityNotExistException("Car not found");
            });

        String existingProducerName = existingCar.getCarModel().getProducer().getName();
        String requestedProducerName = carRequestDTO.getManufacturer();
        if (!existingProducerName.equals(requestedProducerName)) {
            log.error("Car model producer does not match existing car model. Existing producer: {}, Requested producer: {}",
                existingProducerName, requestedProducerName);
            throw new CarModelProducerMismatchException("Car model producer does not match existing car model");
        }
        return existingCar;
    }
}
