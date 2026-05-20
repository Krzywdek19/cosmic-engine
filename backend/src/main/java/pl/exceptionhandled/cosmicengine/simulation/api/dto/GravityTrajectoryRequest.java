package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GravityTrajectoryRequest(
        @NotNull
        @Size(min = 2)
        List<@Valid SimulationBodyRequest> bodies,

        @NotNull @Positive Double deltaTime,

        @NotNull @Min(1) Integer steps
) {
}