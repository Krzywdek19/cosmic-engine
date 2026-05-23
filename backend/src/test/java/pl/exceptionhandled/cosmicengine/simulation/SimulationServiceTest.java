package pl.exceptionhandled.cosmicengine.simulation;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.integrator.ConstantAccelerationStepIntegrator;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.BodyTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;
import pl.exceptionhandled.cosmicengine.simulation.mapper.SimulationBodyMapper;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.policy.MostMassiveBodySelectionPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationServiceTest {

    private final PhysicsEngine physicsEngine = new PhysicsEngine(new ConstantAccelerationStepIntegrator());
    private final SimulationLoop simulationLoop = new SimulationLoop(physicsEngine,new NewtonianGravityCalculator(1.0));
    private final SimulationService simulationService = new SimulationService(simulationLoop, new MostMassiveBodySelectionPolicy(), new SimulationBodyMapper());

    @Test
    void shouldUseMostMassiveBodyAsAttractingBody() {
        GravityTrajectoryRequest request = new GravityTrajectoryRequest(
                List.of(
                        body(10.0, 10.0, 0.0, 0.0, 1.0),
                        body(100.0, 0.0, 0.0, 0.0, 0.0),
                        body(5.0, 15.0, 0.0, 0.0, 0.8)
                ),
                0.1,
                5
        );

        GravityTrajectoryResponse response = simulationService.simulateStaticCentralGravityTrajectory(request);

        assertEquals(1, response.centralBodyIndex());
        assertEquals(new Vector2DResponse(0.0, 0.0), response.centralBodyPosition());
        assertEquals(GravitySimulationModel.STATIC_CENTRAL_BODY, response.model());
        assertEquals(IntegratorType.CONSTANT_ACCELERATION_STEP, response.integrator());
        assertEquals(2, response.trajectories().size());
    }

    @Test
    void shouldReturnCurvedTrajectoryForBodyAffectedByMostMassiveBody() {
        GravityTrajectoryRequest request = new GravityTrajectoryRequest(
                List.of(
                        body(10.0, 10.0, 0.0, 0.0, 1.0),
                        body(100.0, 0.0, 0.0, 0.0, 0.0)
                ),
                0.1,
                5
        );

        GravityTrajectoryResponse response = simulationService.simulateStaticCentralGravityTrajectory(request);

        BodyTrajectoryResponse planetTrajectory = response.trajectories().getFirst();

        Vector2DResponse firstPosition = planetTrajectory.trajectory().getFirst();
        Vector2DResponse lastPosition = planetTrajectory.trajectory()
                .getLast();

        assertEquals(0, planetTrajectory.bodyIndex());
        assertEquals(6, planetTrajectory.trajectory().size());

        assertTrue(lastPosition.x() < firstPosition.x());
        assertTrue(lastPosition.y() > firstPosition.y());
    }

    private SimulationBodyRequest body(
            double mass,
            double positionX,
            double positionY,
            double velocityX,
            double velocityY
    ) {
        return new SimulationBodyRequest(
                mass,
                new Vector2DRequest(positionX, positionY),
                new Vector2DRequest(velocityX, velocityY)
        );
    }
}