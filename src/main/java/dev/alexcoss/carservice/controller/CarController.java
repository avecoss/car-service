package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.request.CarRequestDTO;
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

    @PostMapping("/{manufacturer}/{model}/{year}")
    public ResponseEntity<CarDTO> createCar(@PathVariable String manufacturer, @PathVariable String model,
                                            @PathVariable String year, @RequestBody CarDTO carDTO) {
        CarRequestDTO carRequestDTO = new CarRequestDTO(manufacturer, model, year, carDTO);
        CarDTO createdCar = carService.createCar(carRequestDTO);
        URI location = URI.create(String.format("/api/v1/cars?manufacturer=%s&model=%s&minYear=%s", manufacturer, model, year));
        return ResponseEntity.created(location).body(createdCar);
    }

    @PatchMapping("/{manufacturer}/{model}/{year}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable String manufacturer, @PathVariable String model,
                                            @PathVariable String year, @RequestBody CarDTO carDTO) {
        CarRequestDTO carRequestDTO = new CarRequestDTO(manufacturer, model, year, carDTO);
        return ResponseEntity.ok(carService.updateCar(carRequestDTO));
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

    @DeleteMapping("/{manufacturer}/{model}/{year}")
    public ResponseEntity<Void> deleteCar(@PathVariable String manufacturer, @PathVariable String model, @PathVariable String year) {
        CarRequestDTO carRequestDTO = new CarRequestDTO(manufacturer, model, year);
        carService.deleteCar(carRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
