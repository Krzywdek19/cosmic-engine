package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.integrator.ConstantAccelerationStepIntegrator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.BodyTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;
import pl.exceptionhandled.cosmicengine.simulation.factory.BodySimulationFrameFactory;
import pl.exceptionhandled.cosmicengine.simulation.mapper.SimulationBodyMapper;
import pl.exceptionhandled.cosmicengine.simulation.mapper.TrajectoryFrameMapper;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.policy.MostMassiveBodySelectionPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationServiceTest {

    private static final double EPSILON = 0.000001;

    private final SimulationBodyMapper simulationBodyMapper = new SimulationBodyMapper();

    private final TrajectoryFrameMapper trajectoryFrameMapper =
            new TrajectoryFrameMapper(simulationBodyMapper);

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
            new MostMassiveBodySelectionPolicy(),
            simulationBodyMapper,
            trajectoryFrameMapper
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

        GravityTrajectoryResponse response = simulationService.simulateStaticCentralGravityTrajectory(command);

        assertEquals(GravitySimulationModel.STATIC_CENTRAL_BODY, response.model());
        assertEquals(IntegratorType.CONSTANT_ACCELERATION_STEP, response.integrator());
        assertEquals(1, response.centralBodyIndex());
        assertEquals(new Vector2DResponse(0.0, 0.0), response.centralBodyPosition());
        assertEquals(2, response.trajectories().size());
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

        GravityTrajectoryResponse response = simulationService.simulateStaticCentralGravityTrajectory(command);

        BodyTrajectoryResponse planetTrajectory = response.trajectories().getFirst();

        TrajectoryFrameResponse firstFrame = planetTrajectory.frames().getFirst();
        TrajectoryFrameResponse lastFrame = planetTrajectory.frames().getLast();

        Vector2DResponse firstPosition = firstFrame.position();
        Vector2DResponse lastPosition = lastFrame.position();

        assertEquals(0, planetTrajectory.bodyIndex());
        assertEquals(6, planetTrajectory.frames().size());

        assertEquals(0, firstFrame.step());
        assertEquals(0.0, firstFrame.time(), EPSILON);

        assertEquals(5, lastFrame.step());
        assertEquals(0.5, lastFrame.time(), EPSILON);

        assertTrue(lastPosition.x() < firstPosition.x());
        assertTrue(lastPosition.y() > firstPosition.y());
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