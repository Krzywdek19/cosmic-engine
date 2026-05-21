package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GravityTrajectoryRequest(
        @NotNull
        @Size(min = 2, max = 50)
        List<@Valid SimulationBodyRequest> bodies,

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