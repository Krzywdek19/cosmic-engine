package pl.exceptionhandled.cosmicengine.physics.integrator;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstantAccelerationStepIntegratorTest {

    private static final double EPSILON = 0.000001;

    private final ConstantAccelerationStepIntegrator integrator = new ConstantAccelerationStepIntegrator();

    @Test
    void shouldUpdateBodyPositionAndVelocityUsingConstantAcceleration() {
        Body body = body(
                1.0,
                0.0, 0.0,
                10.0, 0.0,
                2.0, 0.0
        );

        integrator.integrate(body, 1.0);

        assertVectorEquals(new Vector2D(11.0, 0.0), body.getPosition());
        assertVectorEquals(new Vector2D(12.0, 0.0), body.getVelocity());
    }

    @Test
    void shouldThrowExceptionWhenDeltaTimeIsZero() {
        Body body = body(
                1.0,
                0.0, 0.0,
                10.0, 0.0,
                0.0, 0.0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> integrator.integrate(body, 0.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenDeltaTimeIsNegative() {
        Body body = body(
                1.0,
                0.0, 0.0,
                10.0, 0.0,
                0.0, 0.0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> integrator.integrate(body, -1.0)
        );
    }

    private Body body(
            double mass,
            double positionX,
            double positionY,
            double velocityX,
            double velocityY,
            double accelerationX,
            double accelerationY
    ) {
        return new Body(
                mass,
                new Vector2D(positionX, positionY),
                new Vector2D(velocityX, velocityY),
                new Vector2D(accelerationX, accelerationY)
        );
    }

    private void assertVectorEquals(Vector2D expected, Vector2D actual) {
        assertEquals(expected.x(), actual.x(), EPSILON);
        assertEquals(expected.y(), actual.y(), EPSILON);
    }
}