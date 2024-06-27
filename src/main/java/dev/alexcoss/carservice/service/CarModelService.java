package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalEntityException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CarModelDTO createCarModel(String producerName, CarModelDTO carModel) {
        isValidCarModel(carModel);
        Producer producer = producerRepository.findByName(producerName)
            .orElseThrow(() -> new EntityNotExistException("Producer: " + producerName + " not found"));
        carModel.setProducer(modelMapper.map(producer, ProducerDTO.class));

        CarModel savedCarModel = carModelRepository.save(modelMapper.map(carModel, CarModel.class));
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    @Transactional
    public CarModelDTO updateCarModel(String producerName, String currentModelName, CarModelDTO carModel) {
        isValidCarModel(carModel);
        CarModel existingCarModel = carModelRepository.findByProducerNameAndName(producerName, currentModelName)
            .orElseThrow(() -> new EntityNotExistException("Car model: " + currentModelName + " not found"));
        existingCarModel.setName(carModel.getName());

        CarModel savedCarModel = carModelRepository.save(existingCarModel);
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    public Page<CarModelDTO> getListCarModels(String producerName, Pageable pageable) {
        return carModelRepository.findByProducerName(producerName, pageable)
            .map(pageModel -> modelMapper.map(pageModel, CarModelDTO.class));
    }

    @Transactional
    public void deleteCarModel(String producerName, String modelName) {
        CarModel carModel = carModelRepository.findByProducerNameAndName(producerName, modelName)
            .orElseThrow(() -> new EntityNotExistException("Car model: " + modelName + " not found"));
        carModelRepository.delete(carModel);
    }

    private void isValidCarModel(CarModelDTO carModel) {
        if (carModel.getName() == null || carModel.getName().isBlank()) {
            throw new IllegalEntityException("Model: " + carModel.getName() + " not found");
        }
    }
}
