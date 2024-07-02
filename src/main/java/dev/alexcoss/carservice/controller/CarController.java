package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO) {
        CarDTO createdCar = carService.createCar(carDTO);
        URI location = URI.create(String.format("/api/v1/cars?manufacturer=%s&model=%s&minYear=%s",
            carDTO.getCarModel().getProducer().getName(), carDTO.getCarModel().getName(), carDTO.getYear()));
        return ResponseEntity.created(location).body(createdCar);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        carDTO.setId(id);
        return ResponseEntity.ok(carService.updateCar(carDTO));
    }

    @GetMapping
    public ResponseEntity<Page<CarDTO>> listCars(
        @RequestParam(required = false) String manufacturer,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear,
        @RequestParam(required = false) String category,
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(carService.getListCarsWithPagination(manufacturer, model, minYear, maxYear, category, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
