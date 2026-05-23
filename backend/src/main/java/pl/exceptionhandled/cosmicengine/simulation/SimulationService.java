package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.*;
import pl.exceptionhandled.cosmicengine.simulation.mapper.TrajectoryFrameMapper;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.policy.CentralBodySelectionPolicy;
import pl.exceptionhandled.cosmicengine.simulation.mapper.SimulationBodyMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationLoop simulationLoop;
    private final CentralBodySelectionPolicy centralBodySelectionPolicy;
    private final SimulationBodyMapper simulationBodyMapper;
    private final TrajectoryFrameMapper trajectoryFrameMapper;

    public GravityTrajectoryResponse simulateStaticCentralGravityTrajectory(GravityTrajectoryRequest request) {
        List<Body> bodies = request.bodies()
                .stream()
                .map(simulationBodyMapper::toBody)
                .toList();

        int centralBodyIndex = centralBodySelectionPolicy.selectCentralBodyIndex(bodies);

        Body centralBody = bodies.get(centralBodyIndex);

        List<BodyTrajectoryResponse> trajectories = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            if (i == centralBodyIndex) {
                continue;
            }

            Body affectedBody = bodies.get(i);

            List<Vector2D> trajectory = simulationLoop.runStaticCentralGravityTrajectory(
                    affectedBody,
                    centralBody,
                    request.deltaTime(),
                    request.steps()
            );

            List<TrajectoryFrameResponse> frames = trajectoryFrameMapper.toFrames(
                    trajectory,
                    request.deltaTime()
            );

            trajectories.add(new BodyTrajectoryResponse(
                    i,
                    affectedBody.getMass(),
                    frames
            ));
        }

        return new GravityTrajectoryResponse(
                GravitySimulationModel.STATIC_CENTRAL_BODY,
                IntegratorType.CONSTANT_ACCELERATION_STEP,
                centralBodyIndex,
                simulationBodyMapper.toResponse(centralBody.getPosition()),
                trajectories
        );
    }
}