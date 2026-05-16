package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SimpleMotionSimulationRequest(
        @NotNull @Positive Double mass,

        @NotNull @Valid Vector2DRequest position,

        @NotNull @Valid Vector2DRequest velocity,

        @NotNull @Valid Vector2DRequest force,

        @NotNull @Positive Double deltaTime,

        @NotNull @Positive Integer steps
) {
}