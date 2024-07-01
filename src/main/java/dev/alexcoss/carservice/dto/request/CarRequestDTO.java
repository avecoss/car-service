package dev.alexcoss.carservice.dto.request;

import dev.alexcoss.carservice.dto.CarDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestDTO {
    private String manufacturer;
    private String model;
    private String year;
    private CarDTO carDTO;

    public CarRequestDTO(String manufacturer, String model, String year) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.carDTO = new CarDTO();
    }
}
