package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.CarsLinkHelper;
import dev.alexcoss.carservice.dto.CarDTO;
import dev.alexcoss.carservice.dto.CarFilterDTO;
import dev.alexcoss.carservice.service.CarService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Car", description = "Operations related to car")
public class CarController {

    private final CarService carService;
    private final CarsLinkHelper linkHelper;

    @GetMapping("/{id}")
    @Operation(summary = "Get a car by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "404", description = "car not found")
    })
    public ResponseEntity<CarDTO> getCar(@PathVariable Long id) {
        CarDTO carDTO = carService.getCarById(id);
        carDTO.add(linkHelper.createSelfLink(id));
        carDTO.add(linkHelper.createCarsLink());

        return ResponseEntity.ok(carDTO);
    }

    @PostMapping
    @Operation(summary = "Create a new car", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "create car", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403")
    })
    public ResponseEntity<CarDTO> createCar(@RequestBody @Validated CarDTO carDTO) {
        CarDTO createdCar = carService.createCar(carDTO);
        createdCar.add(linkHelper.createSelfLink(createdCar.getId()));
        createdCar.add(linkHelper.createCarsLink());

        URI location = WebMvcLinkBuilder
            .linkTo(CarController.class).slash(createdCar.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdCar);
    }

    @PatchMapping
    @Operation(summary = "Update a car", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<CarDTO> updateCar(@RequestBody @Validated CarDTO carDTO) {
        CarDTO updatedCar = carService.updateCar(carDTO);
        updatedCar.add(linkHelper.createSelfLink(updatedCar.getId()));
        updatedCar.add(linkHelper.createCarsLink());

        return ResponseEntity.ok(updatedCar);
    }

    @GetMapping
    @Operation(summary = "List all cars")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Page<CarDTO>> listCars(
        @RequestParam(required = false) String manufacturer,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear,
        @RequestParam(required = false) String category,
        @PageableDefault(size = 10) Pageable pageable) {

        CarFilterDTO carFilterDTO = CarFilterDTO.builder()
            .manufacturer(manufacturer)
            .model(model)
            .minYear(minYear)
            .maxYear(maxYear)
            .category(category)
            .pageable(pageable)
            .build();

        Page<CarDTO> cars = carService.getListCarsWithPagination(carFilterDTO);

        cars.forEach(carDTO -> {
            carDTO.add(linkHelper.createSelfLink(carDTO.getId()));
            carDTO.add(linkHelper.createCarsLink(carFilterDTO));
        });

        return ResponseEntity.ok(cars);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a car by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "car deleted"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
