package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.integrator.ConstantAccelerationStepIntegrator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;
import pl.exceptionhandled.cosmicengine.simulation.factory.BodySimulationFrameFactory;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.policy.MostMassiveBodySelectionPolicy;
import pl.exceptionhandled.cosmicengine.simulation.result.BodyTrajectoryResult;
import pl.exceptionhandled.cosmicengine.simulation.result.GravityTrajectoryResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationServiceTest {

    private static final double EPSILON = 0.000001;

    private final PhysicsEngine physicsEngine = new PhysicsEngine(
            new ConstantAccelerationStepIntegrator()
    );

    private final SimulationLoop simulationLoop = new SimulationLoop(
            physicsEngine,
            new NewtonianGravityCalculator(1.0),
            new BodySimulationFrameFactory()
    );

    private final SimulationService simulationService = new SimulationService(
            simulationLoop,
            new MostMassiveBodySelectionPolicy()
    );

    @Test
    void shouldUseMostMassiveBodyAsStaticCentralBody() {
        GravityTrajectoryCommand command = new GravityTrajectoryCommand(
                List.of(
                        body(10.0, 10.0, 0.0, 0.0, 1.0),
                        body(100.0, 0.0, 0.0, 0.0, 0.0),
                        body(5.0, 15.0, 0.0, 0.0, 0.8)
                ),
                0.1,
                5
        );

        GravityTrajectoryResult result = simulationService.simulateStaticCentralGravityTrajectory(command);

        assertEquals(GravitySimulationModel.STATIC_CENTRAL_BODY, result.model());
        assertEquals(IntegratorType.CONSTANT_ACCELERATION_STEP, result.integrator());
        assertEquals(1, result.centralBodyIndex());
        assertEquals(new Vector2D(0.0, 0.0), result.centralBodyPosition());
        assertEquals(2, result.trajectories().size());
    }

    @Test
    void shouldReturnCurvedTrajectoryForBodyAffectedByStaticCentralBody() {
        GravityTrajectoryCommand command = new GravityTrajectoryCommand(
                List.of(
                        body(10.0, 10.0, 0.0, 0.0, 1.0),
                        body(100.0, 0.0, 0.0, 0.0, 0.0),
                        body(5.0, 15.0, 0.0, 0.0, 0.8)
                ),
                0.1,
                5
        );

        GravityTrajectoryResult result = simulationService.simulateStaticCentralGravityTrajectory(command);

        BodyTrajectoryResult planetTrajectory = result.trajectories().getFirst();

        BodySimulationFrame firstFrame = planetTrajectory.frames().getFirst();
        BodySimulationFrame lastFrame = planetTrajectory.frames().getLast();

        Vector2D firstPosition = firstFrame.position();
        Vector2D lastPosition = lastFrame.position();

        assertEquals(0, planetTrajectory.bodyIndex());
        assertEquals(6, planetTrajectory.frames().size());

        assertEquals(0, firstFrame.step());
        assertEquals(0.0, firstFrame.time(), EPSILON);

        assertEquals(5, lastFrame.step());
        assertEquals(0.5, lastFrame.time(), EPSILON);

        assertTrue(lastPosition.x() < firstPosition.x());
        assertTrue(lastPosition.y() > firstPosition.y());

        assertEquals(0.0, firstFrame.velocity().x(), EPSILON);
        assertEquals(1.0, firstFrame.velocity().y(), EPSILON);
        assertEquals(0.0, firstFrame.acceleration().x(), EPSILON);
        assertEquals(0.0, firstFrame.acceleration().y(), EPSILON);

        assertTrue(lastFrame.velocity().x() < firstFrame.velocity().x());
        assertTrue(lastFrame.acceleration().x() < 0.0);
    }

    private Body body(
            double mass,
            double positionX,
            double positionY,
            double velocityX,
            double velocityY
    ) {
        return new Body(
                mass,
                new Vector2D(positionX, positionY),
                new Vector2D(velocityX, velocityY),
                Vector2D.ZERO
        );
    }
}