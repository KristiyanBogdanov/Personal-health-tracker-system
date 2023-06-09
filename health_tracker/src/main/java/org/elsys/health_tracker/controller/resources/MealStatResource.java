package org.elsys.health_tracker.controller.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MealStatResource {
    private Long id;

    @NotNull
    @JsonProperty("meal_id")
    private Long mealId;

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotEmpty
    private String date;
}
