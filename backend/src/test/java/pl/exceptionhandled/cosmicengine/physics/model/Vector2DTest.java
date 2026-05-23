package pl.exceptionhandled.cosmicengine.physics.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Vector2DTest {
    @Test
    void shouldAddTwoVectors() {
        Vector2D a = new Vector2D(2,3);
        Vector2D b = new Vector2D(3,2);

        Vector2D result = a.add(b);

        assertEquals(new Vector2D(5,5), result);
    }

    @Test
    void shouldSubtractTwoVectors() {
        Vector2D a = new Vector2D(5,5);
        Vector2D b = new Vector2D(2,3);

        Vector2D result = a.subtract(b);

        assertEquals(new Vector2D(3,2), result);
    }

    @Test
    void shouldMultiplyVectorByScalar() {
        Vector2D a = new Vector2D(2,3);

        Vector2D result = a.multiply(2);

        assertEquals(new Vector2D(4,6), result);
    }

    @Test
    void shouldDivideVectorByScalar() {
        Vector2D a = new Vector2D(4,6);

        Vector2D result = a.divide(2);

        assertEquals(new Vector2D(2,3), result);
    }

    @Test
    void shouldThrowExceptionWhenDivideByZero() {
        Vector2D a = new Vector2D(4,6);

        assertThrows(IllegalArgumentException.class, () -> a.divide(0));
    }

    @Test
    void shouldCalculateMagnitude() {
        Vector2D a = new Vector2D(3,4);

        double result = a.magnitude();

        assertEquals(5, result, 0.000001);
    }

    @Test
    void shouldNormalizeVector() {
        Vector2D vector = new Vector2D(3.0, 4.0);

        Vector2D normalized = vector.normalize();

        assertEquals(0.6, normalized.x(), 0.000001);
        assertEquals(0.8, normalized.y(), 0.000001);
    }

    @Test
    void shouldReturnZeroVectorWhenNormalizingZeroVector() {
        Vector2D a = new Vector2D(0, 0);

        Vector2D result = a.normalize();

        assertEquals(new Vector2D(0, 0), result);
    }

    @Test
    void shouldThrowExceptionWhenNormalizingZeroVector() {
        assertThrows(
                IllegalStateException.class,
                Vector2D.ZERO::normalize
        );
    }

    @Test
    void shouldReturnZeroWhenNormalizeOrZeroIsCalledOnZeroVector() {
        Vector2D normalized = Vector2D.ZERO.normalizeOrZero();

        assertEquals(Vector2D.ZERO, normalized);
    }
}
