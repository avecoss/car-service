package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.dto.ProducerDTO;
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

    @PostMapping
    public ResponseEntity<ProducerDTO> createProducer(@RequestBody ProducerDTO producerDTO) {
        ProducerDTO createdProducer = producerService.createProducer(producerDTO);
        URI location = URI.create("/api/v1/manufacturers");
        return ResponseEntity.created(location).body(createdProducer);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProducerDTO> updateProducer(@PathVariable Long id, @RequestBody ProducerDTO producerDTO) {
        producerDTO.setId(id);
        return ResponseEntity.ok(producerService.updateProducer(producerDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ProducerDTO>> listOfProducers(Pageable pageable) {
        return ResponseEntity.ok(producerService.getListOfProducers(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.noContent().build();
    }
}
