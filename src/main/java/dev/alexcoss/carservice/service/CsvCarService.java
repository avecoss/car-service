package dev.alexcoss.carservice.service;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.alexcoss.carservice.model.*;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.CarRepository;
import dev.alexcoss.carservice.repository.CategoryRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.CsvFileNotFoundException;
import dev.alexcoss.carservice.util.exception.FileReadException;
import dev.alexcoss.carservice.util.exception.IllegalIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvCarService {

    private final CarRepository carRepository;
    private final ProducerRepository producerRepository;
    private final CarModelRepository carModelRepository;
    private final CategoryRepository categoryRepository;

    private final Map<String, Producer> producerCache = new ConcurrentHashMap<>();
    private final Map<String, CarModel> carModelCache = new ConcurrentHashMap<>();
    private final Map<String, Category> categoryCache = new ConcurrentHashMap<>();

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
            log.error(e.getMessage());
            throw new CsvFileNotFoundException(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileReadException(e.getMessage());
        }
    }

    private void saveCarsToDatabase(List<CarCsv> cars) {
        List<Car> carEntities = new ArrayList<>();

        for (CarCsv carCsv : cars) {
            if (carCsv.getObjectId() == null) {
                log.error("Illegal ID. ID cannot be null. Cannot save car.");
                throw new IllegalIdException("Car ID cannot be null");
            }

            Producer producer = getOrCreateProducer(carCsv);
            CarModel model = getOrCreateCarModel(carCsv, producer);
            Set<Category> categories = getOrCreateCategories(carCsv);

            Car car = Car.builder()
                .objectId(carCsv.getObjectId())
                .year(carCsv.getYear())
                .carModel(model)
                .categories(categories)
                .build();

            carEntities.add(car);

        }
        carRepository.saveAll(carEntities);
    }

    private Set<Category> getOrCreateCategories(CarCsv carCsv) {
        Set<Category> categories = new HashSet<>();
        for (String categoryName : carCsv.getCategory().split(",")) {
            categories.add(categoryCache.computeIfAbsent(categoryName.trim(), name -> categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                        .name(name)
                        .build();
                    return categoryRepository.save(newCategory);
                })));
        }
        return categories;
    }

    private CarModel getOrCreateCarModel(CarCsv carCsv, Producer producer) {
        String key = carCsv.getModel() + "-" + producer.getName();
        return carModelCache.computeIfAbsent(key, k -> carModelRepository.findByProducerNameAndName(carCsv.getModel(), producer.getName())
            .orElseGet(() -> {
                CarModel newModel = CarModel.builder()
                    .name(carCsv.getModel())
                    .producer(producer)
                    .build();
                return carModelRepository.save(newModel);
            }));
    }

    private Producer getOrCreateProducer(CarCsv carCsv) {
        return producerCache.computeIfAbsent(carCsv.getMake(), name -> producerRepository.findByName(name)
            .orElseGet(() -> {
                Producer newProducer = Producer.builder()
                    .name(name)
                    .build();
                return producerRepository.save(newProducer);
            }));
    }
}
