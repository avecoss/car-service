package dev.alexcoss.carservice.controller.linkhelper;

import dev.alexcoss.carservice.controller.CarModelController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ModelLinkHelper {
    public Link createSelfLink(Long id) {
        return linkTo(methodOn(CarModelController.class).getCarModel(id)).withSelfRel();
    }

    public Link createModelsLink() {
        return linkTo(methodOn(CarModelController.class).listCarModels(null, Pageable.unpaged())).withRel("models");
    }

    public Link createModelsLink(String manufacturer, Pageable pageable) {
        return linkTo(methodOn(CarModelController.class).listCarModels(manufacturer, pageable)).withRel("models");
    }
}
