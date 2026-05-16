package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import jakarta.validation.constraints.NotNull;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

public record Vector2DRequest(
        @NotNull Double x,
        @NotNull Double y
) {

    public Vector2D toVector2D() {
        return new Vector2D(x, y);
    }
}