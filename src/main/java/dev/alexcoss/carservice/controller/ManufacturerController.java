package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ProducerService producerService;

    @PostMapping("/manufacturers")
    public ResponseEntity<ProducerDTO> createProducer(@RequestBody ProducerDTO producerDTO) {
        return ResponseEntity.ok(producerService.createProducer(producerDTO));
    }

    @PatchMapping("/manufacturers/{name}")
    public ResponseEntity<ProducerDTO> updateProducer(@PathVariable String name, @RequestBody ProducerDTO producerDTO) {
        return ResponseEntity.ok(producerService.updateProducer(name, producerDTO));
    }

    @GetMapping("/manufacturers")
    public ResponseEntity<Page<ProducerDTO>> listOfProducers(Pageable pageable) {
        return ResponseEntity.ok(producerService.getListOfProducers(pageable));
    }

    @DeleteMapping("/manufacturers/{name}")
    public ResponseEntity<Void> deleteProducer(@PathVariable String name) {
        producerService.deleteProducer(name);
        return ResponseEntity.noContent().build();
    }
}
