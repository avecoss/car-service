package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.CarsLinkHelper;
import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarFilterDTO;
import dev.alexcoss.carservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final CarsLinkHelper linkHelper;

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable Long id) {
        CarDTO carDTO = carService.getCarById(id);
        carDTO.add(linkHelper.createSelfLink(id));
        carDTO.add(linkHelper.createCarsLink());

        return ResponseEntity.ok(carDTO);
    }

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody @Validated CarDTO carDTO) {
        CarDTO createdCar = carService.createCar(carDTO);
        createdCar.add(linkHelper.createSelfLink(createdCar.getId()));
        createdCar.add(linkHelper.createCarsLink());

        URI location = WebMvcLinkBuilder
            .linkTo(CarController.class).slash(createdCar.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdCar);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long id, @RequestBody @Validated CarDTO carDTO) {
        carDTO.setId(id);

        CarDTO updatedCar = carService.updateCar(carDTO);
        updatedCar.add(linkHelper.createSelfLink(updatedCar.getId()));
        updatedCar.add(linkHelper.createCarsLink());

        return ResponseEntity.ok(updatedCar);
    }

    @GetMapping
    public ResponseEntity<Page<CarDTO>> listCars(
        @RequestParam(required = false) String manufacturer,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear,
        @RequestParam(required = false) String category,
        @PageableDefault(size = 10) Pageable pageable) {

        CarFilterDTO carFilterDTO = CarFilterDTO.builder()
            .manufacturer(manufacturer)
            .model(model)
            .minYear(minYear)
            .maxYear(maxYear)
            .category(category)
            .pageable(pageable)
            .build();

        Page<CarDTO> cars = carService.getListCarsWithPagination(carFilterDTO);

        cars.forEach(carDTO -> {
            carDTO.add(linkHelper.createSelfLink(carDTO.getId()));
            carDTO.add(linkHelper.createCarsLink(carFilterDTO));
        });

        return ResponseEntity.ok(cars);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
