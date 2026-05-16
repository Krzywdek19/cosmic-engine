package pl.exceptionhandled.cosmicengine.physics.engine;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhysicsEngineTest {

    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    @Test
    void shouldUpdatePositionUsingVelocity() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(10, 0),
                new Vector2D(0, 0)
        );

        physicsEngine.update(body, 2);

        assertEquals(new Vector2D(20, 0), body.getPosition());
        assertEquals(new Vector2D(10, 0), body.getVelocity());
    }

    @Test
    void shouldUpdateVelocityAndPositionUsingAcceleration() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(0, 0),
                new Vector2D(2, 0)
        );

        physicsEngine.update(body, 3);

        assertEquals(new Vector2D(6, 0), body.getVelocity());
        assertEquals(new Vector2D(9, 0), body.getPosition());
    }

    @Test
    void shouldThrowExceptionWhenDeltaTimeIsZero() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(0, 0),
                new Vector2D(0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> physicsEngine.update(body, 0));
    }
}