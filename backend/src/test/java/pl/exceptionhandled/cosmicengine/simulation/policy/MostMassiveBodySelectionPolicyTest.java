package pl.exceptionhandled.cosmicengine.simulation.policy;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MostMassiveBodySelectionPolicyTest {

    private final MostMassiveBodySelectionPolicy policy = new MostMassiveBodySelectionPolicy();

    @Test
    void shouldSelectMostMassiveBodyIndex() {
        List<Body> bodies = List.of(
                body(10.0),
                body(100.0),
                body(50.0)
        );

        int selectedIndex = policy.selectCentralBodyIndex(bodies);

        assertEquals(1, selectedIndex);
    }

    @Test
    void shouldSelectFirstBodyWhenMassesAreEqual() {
        List<Body> bodies = List.of(
                body(100.0),
                body(100.0),
                body(50.0)
        );

        int selectedIndex = policy.selectCentralBodyIndex(bodies);

        assertEquals(0, selectedIndex);
    }

    @Test
    void shouldThrowExceptionWhenBodiesListIsEmpty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> policy.selectCentralBodyIndex(List.of())
        );
    }

    private Body body(double mass) {
        return new Body(
                mass,
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0),
                new Vector2D(0.0, 0.0)
        );
    }
}