package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ManufacturerLinkHelper;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.service.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
@Tag(name = "Manufacturer", description = "Operations related to car manufacturers")
public class ManufacturerController {

    private final ProducerService producerService;
    private final ManufacturerLinkHelper linkHelper;

    @GetMapping("/{id}")
    @Operation(summary = "Get a manufacturer by ID", tags = {"id"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProducerDTO.class))}),
        @ApiResponse(responseCode = "404", description = "manufacturer not found")
    })
    public ResponseEntity<ProducerDTO> getProducer(@PathVariable Long id) {
        ProducerDTO producerDTO = producerService.getProducerById(id);
        producerDTO.add(linkHelper.createSelfLink(id));
        producerDTO.add(linkHelper.createManufacturersLink());

        return ResponseEntity.ok(producerDTO);
    }

    @PostMapping
    @Operation(summary = "Create a new manufacturer", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "create manufacturer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProducerDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403")
    })
    public ResponseEntity<ProducerDTO> createProducer(@RequestBody @Validated ProducerDTO producerDTO) {
        ProducerDTO createdProducer = producerService.createProducer(producerDTO);
        producerDTO.add(linkHelper.createSelfLink(createdProducer.getId()));
        producerDTO.add(linkHelper.createManufacturersLink());

        URI location = WebMvcLinkBuilder
            .linkTo(ManufacturerController.class).slash(createdProducer.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdProducer);
    }

    @PatchMapping
    @Operation(summary = "Update a manufacturer", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProducerDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ProducerDTO> updateProducer(@RequestBody @Validated ProducerDTO producerDTO) {
        ProducerDTO updatedProducerDTO = producerService.updateProducer(producerDTO);
        updatedProducerDTO.add(linkHelper.createSelfLink(updatedProducerDTO.getId()));
        updatedProducerDTO.add(linkHelper.createManufacturersLink());

        return ResponseEntity.ok(updatedProducerDTO);
    }

    @GetMapping
    @Operation(summary = "List all manufacturers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Page<ProducerDTO>> listOfProducers(Pageable pageable) {
        Page<ProducerDTO> producers = producerService.getListOfProducers(pageable);
        producers.forEach(producerDTO -> {
            producerDTO.add(linkHelper.createSelfLink(producerDTO.getId()));
            producerDTO.add(linkHelper.createManufacturersLink(pageable));
        });

        return ResponseEntity.ok(producers);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a manufacturer by ID", tags = {"id"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "manufacturer deleted"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.noContent().build();
    }
}
