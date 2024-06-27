package dev.alexcoss.carservice.repository;

import dev.alexcoss.carservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, String>, JpaSpecificationExecutor<Car> {
    Optional<Car> findByCarModelNameAndYear(String model, String year);
}
