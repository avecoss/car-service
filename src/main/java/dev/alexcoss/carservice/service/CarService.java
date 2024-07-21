package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarFilterDTO;
import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CarSpecification;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> createEntityNotExistException(id));
        return modelMapper.map(car, CarDTO.class);
    }

    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        Car saved = carRepository.save(modelMapper.map(carDTO, Car.class));
        return modelMapper.map(saved, CarDTO.class);
    }

    @Transactional
    public CarDTO updateCar(CarDTO carDTO) {
        Car existingCar = carRepository.findById(carDTO.getId())
            .orElseThrow(() -> createEntityNotExistException(carDTO.getId()));

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(carDTO, existingCar);

        Car updated = carRepository.save(existingCar);
        return modelMapper.map(updated, CarDTO.class);
    }

    public Page<CarDTO> getListCarsWithPagination(CarFilterDTO carFilterDTO) {
        Specification<Car> spec = Specification.where(CarSpecification.hasProducer(carFilterDTO.getManufacturer())
            .and(CarSpecification.hasModel(carFilterDTO.getModel()))
            .and(CarSpecification.hasYearGreaterThanOrEqualTo(carFilterDTO.getMinYear()))
            .and(CarSpecification.hasYearLessThanOrEqualTo(carFilterDTO.getMaxYear()))
            .and(CarSpecification.hasCategory(carFilterDTO.getCategory())));

        Page<Car> carsByFilter = carRepository.findAll(spec, carFilterDTO.getPageable());

        return carsByFilter.map(car -> modelMapper.map(car, CarDTO.class));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCar(Long id) {
        Car existingCar = carRepository.findById(id).orElseThrow(() -> createEntityNotExistException(id));
        carRepository.delete(existingCar);
    }

    private EntityNotExistException createEntityNotExistException(Long id) {
        log.error("Car by id: {} not found", id);
        return new EntityNotExistException("Car not found");
    }
}
