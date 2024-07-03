package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ManufacturerLinkHelper;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ProducerService producerService;
    private final ManufacturerLinkHelper linkHelper;

    public ResponseEntity<ProducerDTO> getProducer(Long id) {
        ProducerDTO producerDTO = producerService.getProducerById(id);
        producerDTO.add(linkHelper.createSelfLink(id));
        producerDTO.add(linkHelper.createManufacturersLink());

        return ResponseEntity.ok(producerDTO);
    }

    @PostMapping
    public ResponseEntity<ProducerDTO> createProducer(@RequestBody @Validated ProducerDTO producerDTO) {
        ProducerDTO createdProducer = producerService.createProducer(producerDTO);
        producerDTO.add(linkHelper.createSelfLink(createdProducer.getId()));
        producerDTO.add(linkHelper.createManufacturersLink());

        URI location = WebMvcLinkBuilder
            .linkTo(ManufacturerController.class).slash(createdProducer.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdProducer);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProducerDTO> updateProducer(@PathVariable Long id, @RequestBody @Validated ProducerDTO producerDTO) {
        producerDTO.setId(id);

        ProducerDTO updatedProducerDTO = producerService.updateProducer(producerDTO);
        updatedProducerDTO.add(linkHelper.createSelfLink(updatedProducerDTO.getId()));
        updatedProducerDTO.add(linkHelper.createManufacturersLink());

        return ResponseEntity.ok(updatedProducerDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProducerDTO>> listOfProducers(Pageable pageable) {
        Page<ProducerDTO> producers = producerService.getListOfProducers(pageable);
        producers.forEach(producerDTO -> {
            producerDTO.add(linkHelper.createSelfLink(producerDTO.getId()));
            producerDTO.add(linkHelper.createManufacturersLink(pageable));
        });

        return ResponseEntity.ok(producers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.noContent().build();
    }
}
