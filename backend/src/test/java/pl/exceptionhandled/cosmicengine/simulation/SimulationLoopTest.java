package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimulationLoopTest {

    private final PhysicsEngine physicsEngine = new PhysicsEngine();
    private final SimulationLoop simulationLoop = new SimulationLoop(physicsEngine);

    @Test
    void shouldRunSimulationForGivenNumberOfSteps() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(10, 0),
                new Vector2D(0, 0)
        );

        simulationLoop.run(body, 1, 5);

        assertEquals(new Vector2D(50, 0), body.getPosition());
    }

    @Test
    void shouldThrowExceptionWhenStepsAreZero() {
        Body body = new Body(
                1,
                new Vector2D(0, 0),
                new Vector2D(0, 0),
                new Vector2D(0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> simulationLoop.run(body, 1, 0));
    }
}