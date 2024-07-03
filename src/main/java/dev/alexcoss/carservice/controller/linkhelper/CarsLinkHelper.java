package dev.alexcoss.carservice.controller.linkhelper;

import dev.alexcoss.carservice.controller.CarController;
import dev.alexcoss.carservice.dto.CarFilterDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarsLinkHelper {
    public Link createSelfLink(Long id) {
        return WebMvcLinkBuilder.linkTo(methodOn(CarController.class).getCar(id)).withSelfRel();
    }

    public Link createCarsLink() {
        return linkTo(methodOn(CarController.class).listCars(null, null, null, null, null, Pageable.unpaged())).withRel("cars");
    }

    public Link createCarsLink(CarFilterDTO carFilter) {
        return linkTo(methodOn(CarController.class).listCars(carFilter.getManufacturer(), carFilter.getModel(), carFilter.getMinYear(), carFilter.getMaxYear(),
            carFilter.getCategory(), carFilter.getPageable())).withRel("cars");
    }
}
