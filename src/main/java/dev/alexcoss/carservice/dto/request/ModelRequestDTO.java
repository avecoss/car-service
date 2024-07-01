package dev.alexcoss.carservice.dto.request;

import dev.alexcoss.carservice.dto.CarModelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelRequestDTO {
    private String manufacturer;
    private String model;
    private CarModelDTO carModelDTO;

    public ModelRequestDTO(String manufacturer, CarModelDTO carModelDTO) {
        this.manufacturer = manufacturer;
        this.carModelDTO = carModelDTO;
    }

    public ModelRequestDTO(String manufacturer, String model) {
        this.manufacturer = manufacturer;
        this.model = model;
    }
}
