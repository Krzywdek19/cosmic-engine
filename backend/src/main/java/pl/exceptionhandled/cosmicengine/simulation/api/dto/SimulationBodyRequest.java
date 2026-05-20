package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SimulationBodyRequest(
        @NotNull @Positive Double mass,

        @NotNull @Valid Vector2DRequest position,

        @NotNull @Valid Vector2DRequest velocity
) {
}