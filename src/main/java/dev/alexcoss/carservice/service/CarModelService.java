package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.dto.request.ModelRequestDTO;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalModelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CarModelDTO createCarModel(ModelRequestDTO modelRequestDTO) {
        isValidCarModel(modelRequestDTO.getCarModelDTO());
        Producer producer = producerRepository.findByName(modelRequestDTO.getManufacturer())
            .orElseThrow(() -> {
                log.error("Cannot find producer with name {}", modelRequestDTO.getManufacturer());
                return new EntityNotExistException("Producer: " + modelRequestDTO.getManufacturer() + " not found");
            });
        modelRequestDTO.getCarModelDTO().setProducer(modelMapper.map(producer, ProducerDTO.class));

        CarModel savedCarModel = carModelRepository.save(modelMapper.map(modelRequestDTO.getCarModelDTO(), CarModel.class));
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    @Transactional
    public CarModelDTO updateCarModel(ModelRequestDTO modelRequestDTO) {
        isValidCarModel(modelRequestDTO.getCarModelDTO());
        CarModel existingCarModel = carModelRepository.findByProducerNameAndName(modelRequestDTO.getManufacturer(), modelRequestDTO.getModel())
            .orElseThrow(() -> getEntityNotExistException(modelRequestDTO));
        existingCarModel.setName(modelRequestDTO.getCarModelDTO().getName());

        CarModel savedCarModel = carModelRepository.save(existingCarModel);
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    public Page<CarModelDTO> getListCarModels(String producerName, Pageable pageable) {
        return carModelRepository.findByProducerName(producerName, pageable)
            .map(pageModel -> modelMapper.map(pageModel, CarModelDTO.class));
    }

    @Transactional
    public void deleteCarModel(ModelRequestDTO modelRequestDTO) {
        CarModel existingCarModel = carModelRepository.findByProducerNameAndName(modelRequestDTO.getManufacturer(), modelRequestDTO.getModel())
            .orElseThrow(() -> getEntityNotExistException(modelRequestDTO));
        carModelRepository.delete(existingCarModel);
    }

    private EntityNotExistException getEntityNotExistException(ModelRequestDTO modelRequestDTO) {
        log.error("Cannot find model with name {}", modelRequestDTO.getModel());
        return new EntityNotExistException("Car model: " + modelRequestDTO.getModel() + " not found");
    }

    private void isValidCarModel(CarModelDTO carModel) {
        if (carModel.getName() == null || carModel.getName().isBlank()) {
            throw new IllegalModelException("Model: " + carModel.getName() + " not found");
        }
    }
}
