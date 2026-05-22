package pl.exceptionhandled.cosmicengine.physics.engine;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.GravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhysicsEngineTest {

    private static final double EPSILON = 0.000001;

    private final PhysicsEngine physicsEngine = new PhysicsEngine(new GravityCalculator(1.0));

    @Test
    void shouldUpdateBodyPositionAndVelocity() {
        Body body = body(
                1.0,
                0.0, 0.0,
                10.0, 0.0,
                2.0, 0.0
        );

        physicsEngine.update(body, 1.0);

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
                () -> physicsEngine.update(body, 0.0)
        );
    }

    @Test
    void shouldApplyTotalForceAsAcceleration() {
        Body body = body(
                10.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        Vector2D force = new Vector2D(20.0, 0.0);

        physicsEngine.applyTotalForce(body, force);

        assertVectorEquals(new Vector2D(2.0, 0.0), body.getAcceleration());
    }

    @Test
    void shouldReplaceAccelerationWhenNewTotalForceIsApplied() {
        Body body = body(
                2.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        physicsEngine.applyTotalForce(body, new Vector2D(10.0, 0.0));
        physicsEngine.applyTotalForce(body, new Vector2D(0.0, 6.0));

        assertVectorEquals(new Vector2D(0.0, 3.0), body.getAcceleration());
    }

    @Test
    void shouldUpdateBodyUsingGravityForce() {
        Body planet = body(
                10.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        Body sun = body(
                20.0,
                10.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        Body updatedPlanet = physicsEngine.updateInStaticGravityField(planet, sun, 1.0);

        assertVectorEquals(new Vector2D(0.2, 0.0), updatedPlanet.getAcceleration());
        assertVectorEquals(new Vector2D(0.2, 0.0), updatedPlanet.getVelocity());
        assertVectorEquals(new Vector2D(0.1, 0.0), updatedPlanet.getPosition());
    }

    @Test
    void shouldApplyMultipleForcesAsTotalAcceleration() {
        Body body = body(
                2.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        physicsEngine.applyForces(
                body,
                List.of(
                        new Vector2D(10.0, 0.0),
                        new Vector2D(0.0, 6.0),
                        new Vector2D(-4.0, 0.0)
                )
        );

        assertVectorEquals(new Vector2D(3.0, 3.0), body.getAcceleration());
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