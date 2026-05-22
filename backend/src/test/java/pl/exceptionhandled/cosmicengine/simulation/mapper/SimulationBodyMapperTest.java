package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationBodyMapperTest {

    private final SimulationBodyMapper mapper = new SimulationBodyMapper();

    @Test
    void shouldMapSimulationBodyRequestToBody() {
        SimulationBodyRequest request = new SimulationBodyRequest(
                10.0,
                new Vector2DRequest(1.0, 2.0),
                new Vector2DRequest(3.0, 4.0)
        );

        Body body = mapper.toBody(request);

        assertEquals(10.0, body.getMass());
        assertEquals(new Vector2D(1.0, 2.0), body.getPosition());
        assertEquals(new Vector2D(3.0, 4.0), body.getVelocity());
        assertEquals(Vector2D.ZERO, body.getAcceleration());
    }

    @Test
    void shouldMapVector2DRequestToVector2D() {
        Vector2D vector = mapper.toVector2D(new Vector2DRequest(5.0, 6.0));

        assertEquals(new Vector2D(5.0, 6.0), vector);
    }

    @Test
    void shouldMapVector2DToVector2DResponse() {
        Vector2DResponse response = mapper.toResponse(new Vector2D(7.0, 8.0));

        assertEquals(7.0, response.x());
        assertEquals(8.0, response.y());
    }
}