package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ModelLinkHelper;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.service.CarModelService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Tag(name = "CarModel", description = "Operations related to car model")
public class CarModelController {

    private final CarModelService carModelService;
    private final ModelLinkHelper linkHelper;

    @GetMapping("/{id}")
    @Operation(summary = "Get a model by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarModelDTO.class))),
        @ApiResponse(responseCode = "404", description = "manufacturer not found")
    })
    public ResponseEntity<CarModelDTO> getCarModel(@PathVariable Long id) {
        CarModelDTO carModelDTO = carModelService.getCarModelById(id);
        carModelDTO.add(linkHelper.createSelfLink(id));
        carModelDTO.add(linkHelper.createModelsLink());

        return ResponseEntity.ok(carModelDTO);
    }

    @PostMapping
    @Operation(summary = "Create a new model", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "create model", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarModelDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403")
    })
    public ResponseEntity<CarModelDTO> createCarModel(@RequestBody @Validated CarModelDTO carModelDTO) {
        CarModelDTO createdCarModel = carModelService.createCarModel(carModelDTO);
        createdCarModel.add(linkHelper.createSelfLink(createdCarModel.getId()));
        createdCarModel.add(linkHelper.createModelsLink());

        URI location = WebMvcLinkBuilder
            .linkTo(CarModelController.class).slash(createdCarModel.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdCarModel);
    }

    @PatchMapping
    @Operation(summary = "Update a model", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarModelDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<CarModelDTO> updateCarModel(@RequestBody @Validated CarModelDTO carModelDTO) {
        CarModelDTO updatedCarModel = carModelService.updateCarModel(carModelDTO);
        updatedCarModel.add(linkHelper.createSelfLink(carModelDTO.getId()));
        updatedCarModel.add(linkHelper.createModelsLink());

        return ResponseEntity.ok(updatedCarModel);
    }

    @GetMapping
    @Operation(summary = "List all models")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Page<CarModelDTO>> listCarModels(@RequestParam(required = false) String manufacturer, Pageable pageable) {
        Page<CarModelDTO> models = carModelService.getListCarModels(manufacturer, pageable);
        models.forEach(carModelDTO -> {
            carModelDTO.add(linkHelper.createSelfLink(carModelDTO.getId()));
            carModelDTO.add(linkHelper.createModelsLink(manufacturer, pageable));
        });

        return ResponseEntity.ok(models);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a model by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "model deleted"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCarModel(@PathVariable Long id) {
        carModelService.deleteCarModel(id);
        return ResponseEntity.noContent().build();
    }
}
