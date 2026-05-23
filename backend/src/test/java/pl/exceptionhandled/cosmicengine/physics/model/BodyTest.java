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

    @Test
    void shouldThrowExceptionWhenMassIsNotFinite() {
        assertThrows(
                IllegalArgumentException.class,
                () -> body(Double.NaN)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> body(Double.POSITIVE_INFINITY)
        );
    }

    @Test
    void shouldThrowExceptionWhenPositionIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Body(
                        1.0,
                        null,
                        Vector2D.ZERO,
                        Vector2D.ZERO
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenVelocityIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Body(
                        1.0,
                        Vector2D.ZERO,
                        null,
                        Vector2D.ZERO
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccelerationIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Body(
                        1.0,
                        Vector2D.ZERO,
                        Vector2D.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenPositionIsSetToNull() {
        Body body = body(1.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> body.setPosition(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenVelocityIsSetToNull() {
        Body body = body(1.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> body.setVelocity(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenAccelerationIsSetToNull() {
        Body body = body(1.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> body.setAcceleration(null)
        );
    }

    private Body body(double mass) {
        return new Body(
                mass,
                Vector2D.ZERO,
                Vector2D.ZERO,
                Vector2D.ZERO
        );
    }
}