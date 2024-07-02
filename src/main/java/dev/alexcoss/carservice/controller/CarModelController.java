package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;

    @PostMapping
    public ResponseEntity<CarModelDTO> createCarModel(@RequestBody CarModelDTO carModelDTO) {
        CarModelDTO createdCarModel = carModelService.createCarModel(carModelDTO);
        URI location = URI.create("/api/v1/models");
        return ResponseEntity.created(location).body(createdCarModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarModelDTO> updateCarModel(@PathVariable Long id, @RequestBody CarModelDTO carModelDTO) {
        carModelDTO.setId(id);
        return ResponseEntity.ok(carModelService.updateCarModel(carModelDTO));
    }

    @GetMapping
    public ResponseEntity<Page<CarModelDTO>> listCarModels(@RequestParam(required = false) String manufacturer, Pageable pageable) {
        return ResponseEntity.ok(carModelService.getListCarModels(manufacturer, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable Long id) {
        carModelService.deleteCarModel(id);
        return ResponseEntity.noContent().build();
    }
}
