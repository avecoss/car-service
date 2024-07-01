package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.request.ModelRequestDTO;
import dev.alexcoss.carservice.service.CarModelService;
import dev.alexcoss.carservice.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ProducerService producerService;
    private final CarModelService carModelService;

    @PostMapping
    public ResponseEntity<ProducerDTO> createProducer(@RequestBody ProducerDTO producerDTO) {
        ProducerDTO createdProducer = producerService.createProducer(producerDTO);
        URI location = URI.create(String.format("/api/v1/manufacturers/%s", createdProducer.getName()));
        return ResponseEntity.created(location).body(createdProducer);
    }

    @PatchMapping("/{name}")
    public ResponseEntity<ProducerDTO> updateProducer(@PathVariable String name, @RequestBody ProducerDTO producerDTO) {
        return ResponseEntity.ok(producerService.updateProducer(name, producerDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ProducerDTO>> listOfProducers(Pageable pageable) {
        return ResponseEntity.ok(producerService.getListOfProducers(pageable));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteProducer(@PathVariable String name) {
        producerService.deleteProducer(name);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/{manufacturer}/models")
    public ResponseEntity<CarModelDTO> createCarModel(@PathVariable String manufacturer, @RequestBody CarModelDTO carModelDTO) {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO(manufacturer, carModelDTO);
        CarModelDTO createdCarModel = carModelService.createCarModel(modelRequestDTO);
        URI location = URI.create(String.format("/api/v1/manufacturers/%s/models", manufacturer));
        return ResponseEntity.created(location).body(createdCarModel);
    }

    @PatchMapping("/{manufacturer}/models/{model}")
    public ResponseEntity<CarModelDTO> updateCarModel(@PathVariable String manufacturer, @PathVariable String model,
                                                      @RequestBody CarModelDTO carModelDTO) {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO(manufacturer, model, carModelDTO);
        return ResponseEntity.ok(carModelService.updateCarModel(modelRequestDTO));
    }

    @GetMapping("/{manufacturer}/models")
    public ResponseEntity<Page<CarModelDTO>> listCarModels(@PathVariable String manufacturer, Pageable pageable) {
        return ResponseEntity.ok(carModelService.getListCarModels(manufacturer, pageable));
    }

    @DeleteMapping("/{manufacturer}/models/{model}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable String manufacturer, @PathVariable String model) {
        ModelRequestDTO modelRequestDTO = new ModelRequestDTO(manufacturer, model);
        carModelService.deleteCarModel(modelRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
