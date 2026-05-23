package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.result.BodyTrajectoryResult;
import pl.exceptionhandled.cosmicengine.simulation.result.GravityTrajectoryResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GravityTrajectoryResponseMapperTest {

    private final SimulationBodyMapper simulationBodyMapper = new SimulationBodyMapper();

    private final TrajectoryFrameMapper trajectoryFrameMapper =
            new TrajectoryFrameMapper(simulationBodyMapper);

    private final GravityTrajectoryResponseMapper mapper =
            new GravityTrajectoryResponseMapper(
                    simulationBodyMapper,
                    trajectoryFrameMapper
            );

    @Test
    void shouldMapGravityTrajectoryResultToResponse() {
        GravityTrajectoryResult result = new GravityTrajectoryResult(
                GravitySimulationModel.STATIC_CENTRAL_BODY,
                IntegratorType.CONSTANT_ACCELERATION_STEP,
                1,
                new Vector2D(0.0, 0.0),
                List.of(
                        new BodyTrajectoryResult(
                                0,
                                10.0,
                                List.of(
                                        new BodySimulationFrame(
                                                0,
                                                0.0,
                                                new Vector2D(10.0, 0.0),
                                                new Vector2D(0.0, 1.0),
                                                Vector2D.ZERO
                                        )
                                )
                        )
                )
        );

        GravityTrajectoryResponse response = mapper.toResponse(result);

        assertEquals(GravitySimulationModel.STATIC_CENTRAL_BODY, response.model());
        assertEquals(IntegratorType.CONSTANT_ACCELERATION_STEP, response.integrator());
        assertEquals(1, response.centralBodyIndex());
        assertEquals(0.0, response.centralBodyPosition().x());
        assertEquals(0.0, response.centralBodyPosition().y());

        assertEquals(1, response.trajectories().size());
        assertEquals(0, response.trajectories().getFirst().bodyIndex());
        assertEquals(10.0, response.trajectories().getFirst().mass());

        assertEquals(1, response.trajectories().getFirst().frames().size());
        assertEquals(10.0, response.trajectories().getFirst().frames().getFirst().position().x());
        assertEquals(1.0, response.trajectories().getFirst().frames().getFirst().velocity().y());
    }
}