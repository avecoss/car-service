package dev.alexcoss.carservice.repository;

import dev.alexcoss.carservice.model.Car;
import dev.alexcoss.carservice.model.Category;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

    public static Specification<Car> hasProducer(String producer) {
        return (root, query, builder) ->
            producer == null ? builder.conjunction() : builder.equal(root.get("carModel").get("producer").get("name"), producer);
    }

    public static Specification<Car> hasModel(String model) {
        return (root, query, builder) ->
            model == null ? builder.conjunction() : builder.equal(root.get("carModel").get("name"), model);
    }

    public static Specification<Car> hasYearGreaterThanOrEqualTo(Integer minYear) {
        return (root, query, builder) ->
            minYear == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("year"), minYear.toString());
    }

    public static Specification<Car> hasYearLessThanOrEqualTo(Integer maxYear) {
        return (root, query, builder) ->
            maxYear == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("year"), maxYear.toString());
    }

    public static Specification<Car> hasCategory(String category) {
        return (root, query, builder) -> {
            if (category == null) {
                return builder.conjunction();
            } else {
                Join<Car, Category> categories = root.join("categories");
                return builder.equal(categories.get("name"), category);
            }
        };
    }
}
