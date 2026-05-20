package pl.exceptionhandled.cosmicengine.physics;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GravityCalculatorTest {

    @Test
    void shouldCalculateGravitationalForceBetweenTwoBodies() throws IllegalAccessException {
        GravityCalculator gravityCalculator = new GravityCalculator(1.0);

        Body affectedBody = new Body(
                10.0,
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        Body attractingBody = new Body(
                20.0,
                new Vector2D(10.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        Vector2D force = gravityCalculator.calculateForce(affectedBody, attractingBody);

        assertEquals(2.0, force.x(), 0.000001);
        assertEquals(0.0, force.y(), 0.000001);
    }

    @Test
    void shouldThrowExceptionWhenBodiesHaveTheSamePosition() {
        GravityCalculator gravityCalculator = new GravityCalculator(1.0);

        Body affectedBody = new Body(
                10.0,
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        Body attractingBody = new Body(
                20.0,
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> gravityCalculator.calculateForce(affectedBody, attractingBody)
        );
    }

    @Test
    void shouldCalculateEqualAndOppositeForces() {
        GravityCalculator gravityCalculator = new GravityCalculator(1.0);

        Body aBody = new Body(
                10.0,
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        Body bBody = new Body(
                20.0,
                new Vector2D(10.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );

        Vector2D forceOnA = gravityCalculator.calculateForce(aBody, bBody);
        Vector2D forceOnB = gravityCalculator.calculateForce(bBody, aBody);

        assertEquals(-forceOnA.x(), forceOnB.x(), 0.000001);
        assertEquals(-forceOnA.y(), forceOnB.y(), 0.000001);
    }
}