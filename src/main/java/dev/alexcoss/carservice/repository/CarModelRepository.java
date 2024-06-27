package dev.alexcoss.carservice.repository;

import dev.alexcoss.carservice.model.CarModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    Optional<CarModel> findByName(String model);

    Optional<CarModel> findByProducerNameAndName(String producerName, String name);

    Page<CarModel> findByProducerName(String producerName, Pageable pageable);
}
