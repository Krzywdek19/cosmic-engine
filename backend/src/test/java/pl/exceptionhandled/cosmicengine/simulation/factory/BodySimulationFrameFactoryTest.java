package pl.exceptionhandled.cosmicengine.simulation.factory;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BodySimulationFrameFactoryTest {

    private final BodySimulationFrameFactory factory = new BodySimulationFrameFactory();

    @Test
    void shouldCreateBodySimulationFrameFromBodyState() {
        Body body = new Body(
                10.0,
                new Vector2D(1.0, 2.0),
                new Vector2D(3.0, 4.0),
                new Vector2D(5.0, 6.0)
        );

        BodySimulationFrame frame = factory.create(2, 0.5, body);

        assertEquals(2, frame.step());
        assertEquals(0.5, frame.time());
        assertEquals(new Vector2D(1.0, 2.0), frame.position());
        assertEquals(new Vector2D(3.0, 4.0), frame.velocity());
        assertEquals(new Vector2D(5.0, 6.0), frame.acceleration());
    }
}