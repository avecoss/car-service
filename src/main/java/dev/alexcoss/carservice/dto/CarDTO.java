package dev.alexcoss.carservice.dto;

import jakarta.validation.constraints.NotNull;
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
    private Long id;
    private String objectId;
    @Pattern(regexp = "^\\d{4}$")
    private String year;
    @NotNull
    private CarModelDTO carModel;
    @Builder.Default
    private Set<CategoryDTO> categories = new HashSet<>();
}
