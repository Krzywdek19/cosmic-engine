package pl.exceptionhandled.cosmicengine.physics.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BodyTest {

    @Test
    void shouldCreateBody() {
        Vector2D position = new Vector2D(0, 0);
        Vector2D velocity = new Vector2D(10, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        Body body = new Body(5, position, velocity, acceleration);

        assertEquals(5, body.getMass());
        assertEquals(position, body.getPosition());
        assertEquals(velocity, body.getVelocity());
        assertEquals(acceleration, body.getAcceleration());
    }

    @Test
    void shouldThrowExceptionWhenMassIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Body(
                        0,
                        new Vector2D(0, 0),
                        new Vector2D(0, 0),
                        new Vector2D(0, 0)
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenMassIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Body(
                        -1,
                        new Vector2D(0, 0),
                        new Vector2D(0, 0),
                        new Vector2D(0, 0)
                )
        );
    }

    @Test
    void shouldUpdatePosition() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(0, 0),
                new Vector2D(0, 0)
        );

        body.setPosition(new Vector2D(10, 20));

        assertEquals(new Vector2D(10, 20), body.getPosition());
    }
}