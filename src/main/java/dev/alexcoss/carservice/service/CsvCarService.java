package dev.alexcoss.carservice.service;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.alexcoss.carservice.model.*;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CategoryRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.CsvFileNotFoundException;
import dev.alexcoss.carservice.util.exception.DuplicateIdException;
import dev.alexcoss.carservice.util.exception.FileReadException;
import dev.alexcoss.carservice.util.exception.IllegalIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CsvCarService {

    private final CarRepository carRepository;
    private final ProducerRepository producerRepository;
    private final CarModelRepository carModelRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void parseAndSaveCars(String filePath) {
        List<CarCsv> cars = parseCarsFromFile(filePath);
        saveCarsToDatabase(cars);
    }

    private List<CarCsv> parseCarsFromFile(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            return new CsvToBeanBuilder<CarCsv>(reader)
                .withType(CarCsv.class)
                .build()
                .parse();

        } catch (FileNotFoundException e) {
            throw new CsvFileNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new FileReadException(e.getMessage());
        }
    }

    private void saveCarsToDatabase(List<CarCsv> cars) {
        for (CarCsv carCsv : cars) {
            if (carCsv.getObjectId() == null)
                throw new IllegalIdException("Car ID cannot be null");

            if (carRepository.existsById(carCsv.getObjectId()))
                throw new DuplicateIdException("Duplicate ID: " + carCsv.getObjectId());

            Producer producer = getOrCreateProducer(carCsv);
            System.out.println(producer);
            CarModel model = getOrCreateCarModel(carCsv, producer);
            Set<Category> categories = getOrCreateCategories(carCsv);

            Car car = Car.builder()
                .id(carCsv.getObjectId())
                .year(carCsv.getYear())
                .carModel(model)
                .categories(categories)
                .build();

            carRepository.saveAndFlush(car); //todo batch update !
        }
    }

    private Set<Category> getOrCreateCategories(CarCsv carCsv) {
        Set<Category> categories = new HashSet<>();
        for (String categoryName : carCsv.getCategory().split(",")) {
            Category category = categoryRepository.findByName(categoryName.trim())
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                        .name(categoryName.trim())
                        .build();
                    return categoryRepository.save(newCategory);
                });
            categories.add(category);
        }
        return categories;
    }

    private CarModel getOrCreateCarModel(CarCsv carCsv, Producer producer) {
        return carModelRepository.findByName(carCsv.getModel())
            .orElseGet(() -> {
                CarModel newModel = CarModel.builder()
                    .name(carCsv.getModel())
                    .producer(producer)
                    .build();
                return carModelRepository.save(newModel);
            });
    }

    private Producer getOrCreateProducer(CarCsv carCsv) {
        return producerRepository.findByName(carCsv.getMake())
            .orElseGet(() -> {
                Producer newProducer = Producer.builder()
                    .name(carCsv.getMake())
                    .build();
                return producerRepository.save(newProducer);
            });
    }
}
