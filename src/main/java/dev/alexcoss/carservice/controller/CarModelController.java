package dev.alexcoss.carservice.controller;

import dev.alexcoss.carservice.controller.linkhelper.ModelLinkHelper;
import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;
    private final ModelLinkHelper linkHelper;

    @GetMapping("/{id}")
    public ResponseEntity<CarModelDTO> getCarModel(@PathVariable Long id) {
        CarModelDTO carModelDTO = carModelService.getCarModelById(id);
        carModelDTO.add(linkHelper.createSelfLink(id));
        carModelDTO.add(linkHelper.createModelsLink());

        return ResponseEntity.ok(carModelDTO);
    }

    @PostMapping
    public ResponseEntity<CarModelDTO> createCarModel(@RequestBody @Validated CarModelDTO carModelDTO) {
        CarModelDTO createdCarModel = carModelService.createCarModel(carModelDTO);
        createdCarModel.add(linkHelper.createSelfLink(createdCarModel.getId()));
        createdCarModel.add(linkHelper.createModelsLink());

        URI location = WebMvcLinkBuilder
            .linkTo(CarModelController.class).slash(createdCarModel.getId()).withSelfRel()
            .toUri();

        return ResponseEntity.created(location).body(createdCarModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarModelDTO> updateCarModel(@PathVariable Long id, @RequestBody @Validated CarModelDTO carModelDTO) {
        carModelDTO.setId(id);

        CarModelDTO updatedCarModel = carModelService.updateCarModel(carModelDTO);
        updatedCarModel.add(linkHelper.createSelfLink(id));
        updatedCarModel.add(linkHelper.createModelsLink());

        return ResponseEntity.ok(updatedCarModel);
    }

    @GetMapping
    public ResponseEntity<Page<CarModelDTO>> listCarModels(@RequestParam(required = false) String manufacturer, Pageable pageable) {
        Page<CarModelDTO> models = carModelService.getListCarModels(manufacturer, pageable);
        models.forEach(carModelDTO -> {
            carModelDTO.add(linkHelper.createSelfLink(carModelDTO.getId()));
            carModelDTO.add(linkHelper.createModelsLink(manufacturer, pageable));
        });

        return ResponseEntity.ok(models);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable Long id) {
        carModelService.deleteCarModel(id);
        return ResponseEntity.noContent().build();
    }
}
