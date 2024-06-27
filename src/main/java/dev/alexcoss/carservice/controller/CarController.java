package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<CarDTO> createCar(@PathVariable String manufacturer, @PathVariable String model, @PathVariable String year,
                                            @RequestBody CarDTO carDTO) {
        return ResponseEntity.ok(carService.createCar(manufacturer, model, year, carDTO));
    }

    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable String manufacturer, @PathVariable String model, @PathVariable String year,
                                            @RequestBody CarDTO carDTO) {
        return ResponseEntity.ok(carService.updateCar(manufacturer, model, year, carDTO));
    }

    @GetMapping("/cars")
    public ResponseEntity<Page<CarDTO>> listCars(
        @RequestParam(required = false) String manufacturer,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear,
        @RequestParam(required = false) String category,
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(carService.getListCarsWithPagination(manufacturer, model, minYear, maxYear, category, pageable));
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
