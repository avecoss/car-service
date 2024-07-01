package dev.alexcoss.carservice.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
