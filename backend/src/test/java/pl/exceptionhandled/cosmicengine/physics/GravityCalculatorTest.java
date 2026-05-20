package pl.exceptionhandled.cosmicengine.physics;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GravityCalculatorTest {

    private static final double EPSILON = 0.000001;

    private final GravityCalculator gravityCalculator = new GravityCalculator(1.0);

    @Test
    void shouldCalculateGravitationalForceBetweenTwoBodies() {
        Body affectedBody = body(10.0, 0.0, 0.0);
        Body attractingBody = body(20.0, 10.0, 0.0);

        Vector2D force = gravityCalculator.calculateForce(affectedBody, attractingBody);

        assertVectorEquals(new Vector2D(2.0, 0.0), force);
    }

    @Test
    void shouldThrowExceptionWhenBodiesHaveTheSamePosition() {
        Body affectedBody = body(10.0, 0.0, 0.0);
        Body attractingBody = body(20.0, 0.0, 0.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> gravityCalculator.calculateForce(affectedBody, attractingBody)
        );
    }

    @Test
    void shouldCalculateEqualAndOppositeForces() {
        Body aBody = body(10.0, 0.0, 0.0);
        Body bBody = body(20.0, 10.0, 0.0);

        Vector2D forceOnA = gravityCalculator.calculateForce(aBody, bBody);
        Vector2D forceOnB = gravityCalculator.calculateForce(bBody, aBody);

        assertEquals(-forceOnA.x(), forceOnB.x(), EPSILON);
        assertEquals(-forceOnA.y(), forceOnB.y(), EPSILON);
    }

    private Body body(double mass, double positionX, double positionY) {
        return new Body(
                mass,
                new Vector2D(positionX, positionY),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );
    }

    private void assertVectorEquals(Vector2D expected, Vector2D actual) {
        assertEquals(expected.x(), actual.x(), EPSILON);
        assertEquals(expected.y(), actual.y(), EPSILON);
    }
}