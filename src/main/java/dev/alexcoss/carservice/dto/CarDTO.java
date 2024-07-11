package dev.alexcoss.carservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "car information")
public class CarDTO extends RepresentationModel<CarDTO> {
    private Long id;
    private String objectId;
    @Pattern(regexp = "^\\d{4}$")
    private String year;
    @NotNull
    private CarModelDTO carModel;
    @Builder.Default
    private Set<CategoryDTO> categories = new HashSet<>();
}
