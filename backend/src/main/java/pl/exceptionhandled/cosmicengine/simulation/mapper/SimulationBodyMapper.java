package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;

@Component
public class SimulationBodyMapper {

    public Body toBody(SimulationBodyRequest request) {
        return new Body(
                request.mass(),
                toVector2D(request.position()),
                toVector2D(request.velocity()),
                Vector2D.ZERO
        );
    }

    public Vector2D toVector2D(Vector2DRequest request) {
        return new Vector2D(
                request.x(),
                request.y()
        );
    }

    public Vector2DResponse toResponse(Vector2D vector) {
        return new Vector2DResponse(
                vector.x(),
                vector.y()
        );
    }
}