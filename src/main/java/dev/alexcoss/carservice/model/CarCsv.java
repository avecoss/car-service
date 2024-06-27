package dev.alexcoss.carservice.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CarCsv {

    @CsvBindByName(column = "objectId")
    private String objectId;

    @CsvBindByName(column = "Make")
    private String make;

    @CsvBindByName(column = "Year")
    private String year;

    @CsvBindByName(column = "Model")
    private String model;

    @CsvBindByName(column = "Category")
    private String category;
}
