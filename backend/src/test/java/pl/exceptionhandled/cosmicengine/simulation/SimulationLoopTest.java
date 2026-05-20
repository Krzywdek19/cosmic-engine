package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.GravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimulationLoopTest {

    private final PhysicsEngine physicsEngine = new PhysicsEngine(new GravityCalculator(1.0));
    private final SimulationLoop simulationLoop = new SimulationLoop(physicsEngine);

    @Test
    void shouldRunSimulationForGivenNumberOfSteps() {
        Body body = body(
                1.0,
                0.0, 0.0,
                10.0, 0.0,
                0.0, 0.0
        );

        simulationLoop.run(body, 1.0, 5);

        assertEquals(new Vector2D(50.0, 0.0), body.getPosition());
    }

    @Test
    void shouldThrowExceptionWhenStepsAreZero() {
        Body body = body(
                1.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> simulationLoop.run(body, 1.0, 0)
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
}