package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SimpleMotionSimulationRequest(
        @NotNull @Positive Double mass,

        @NotNull @Valid Vector2DRequest position,

        @NotNull @Valid Vector2DRequest velocity,

        @NotNull @Valid Vector2DRequest force,

        @NotNull
        @Positive
        @DecimalMax("1000.0")
        Double deltaTime,

        @NotNull
        @Min(1)
        @Max(10_000)
        Integer steps
) {
}