package dev.alexcoss.carservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {
    private String id;
    @Pattern(regexp = "^\\d{4}$")
    private String year;
    private CarModelDTO carModel;
    @Builder.Default
    private Set<CategoryDTO> categories = new HashSet<>();
}
