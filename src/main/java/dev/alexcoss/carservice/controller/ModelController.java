package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manufacturers/{manufacturer}")
@RequiredArgsConstructor
public class ModelController {

    private final CarModelService carModelService;

    @PostMapping("/models")
    public ResponseEntity<CarModelDTO> createCarModel(@PathVariable String manufacturer, @RequestBody CarModelDTO carModelDTO) {
        return ResponseEntity.ok(carModelService.createCarModel(manufacturer, carModelDTO));
    }

    @PatchMapping("/models/{model}")
    public ResponseEntity<CarModelDTO> updateCarModel(@PathVariable String manufacturer, @PathVariable String model,
                                                      @RequestBody CarModelDTO carModelDTO) {
        return ResponseEntity.ok(carModelService.updateCarModel(manufacturer, model, carModelDTO));
    }

    @GetMapping("/models")
    public ResponseEntity<Page<CarModelDTO>> listCarModels(@PathVariable String manufacturer, Pageable pageable) {
        return ResponseEntity.ok(carModelService.getListCarModels(manufacturer, pageable));
    }

    @DeleteMapping("/models/{model}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable String manufacturer, @PathVariable String model) {
        carModelService.deleteCarModel(manufacturer, model);
        return ResponseEntity.noContent().build();
    }
}
