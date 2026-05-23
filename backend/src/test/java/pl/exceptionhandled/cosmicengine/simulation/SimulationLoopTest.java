package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.integrator.ConstantAccelerationStepIntegrator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.factory.BodySimulationFrameFactory;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationLoopTest {

    private static final double EPSILON = 0.000001;

    private final PhysicsEngine physicsEngine = new PhysicsEngine(
            new ConstantAccelerationStepIntegrator()
    );

    private final SimulationLoop simulationLoop = new SimulationLoop(
            physicsEngine,
            new NewtonianGravityCalculator(1.0),
            new BodySimulationFrameFactory()
    );

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

    @Test
    void shouldRunStaticCentralGravitySimulationForOneStep() {
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

        simulationLoop.runStaticCentralGravity(planet, sun, 1.0, 1);

        assertVectorEquals(new Vector2D(0.1, 0.0), planet.getPosition());
        assertVectorEquals(new Vector2D(0.2, 0.0), planet.getVelocity());
        assertVectorEquals(new Vector2D(0.2, 0.0), planet.getAcceleration());
    }

    @Test
    void shouldCurveMovingBodyTrajectoryUsingStaticCentralGravity() {
        Body planet = body(
                10.0,
                10.0, 0.0,
                0.0, 1.0,
                0.0, 0.0
        );

        Body sun = body(
                20.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        simulationLoop.runStaticCentralGravity(planet, sun, 1.0, 5);

        assertTrue(planet.getPosition().x() < 10.0);
        assertTrue(planet.getPosition().y() > 0.0);
    }

    @Test
    void shouldReturnStaticCentralGravityFrames() {
        Body planet = body(
                10.0,
                10.0, 0.0,
                0.0, 1.0,
                0.0, 0.0
        );

        Body sun = body(
                100.0,
                0.0, 0.0,
                0.0, 0.0,
                0.0, 0.0
        );

        List<BodySimulationFrame> frames = simulationLoop.runStaticCentralGravityFrames(
                planet,
                sun,
                1.0,
                1
        );

        assertEquals(2, frames.size());

        BodySimulationFrame initialFrame = frames.getFirst();
        BodySimulationFrame nextFrame = frames.getLast();

        assertEquals(0, initialFrame.step());
        assertEquals(0.0, initialFrame.time(), EPSILON);
        assertVectorEquals(new Vector2D(10.0, 0.0), initialFrame.position());
        assertVectorEquals(new Vector2D(0.0, 1.0), initialFrame.velocity());
        assertVectorEquals(Vector2D.ZERO, initialFrame.acceleration());

        assertEquals(1, nextFrame.step());
        assertEquals(1.0, nextFrame.time(), EPSILON);

        assertTrue(nextFrame.position().x() < initialFrame.position().x());
        assertTrue(nextFrame.position().y() > initialFrame.position().y());
        assertTrue(nextFrame.velocity().x() < initialFrame.velocity().x());
        assertTrue(nextFrame.acceleration().x() < 0.0);
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