package dev.alexcoss.carservice.controller.linkhelper;

import dev.alexcoss.carservice.controller.ManufacturerController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ManufacturerLinkHelper {
    public Link createSelfLink(Long id) {
        return linkTo(methodOn(ManufacturerController.class).getProducer(id)).withSelfRel();
    }

    public Link createManufacturersLink() {
        return linkTo(methodOn(ManufacturerController.class).listOfProducers(Pageable.unpaged())).withRel("manufacturers");
    }

    public Link createManufacturersLink(Pageable pageable) {
        return linkTo(methodOn(ManufacturerController.class).listOfProducers(pageable)).withRel("manufacturers");
    }
}
