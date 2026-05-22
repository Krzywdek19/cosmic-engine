package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.BodyTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;
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

            List<Vector2DResponse> trajectory = simulationLoop.runStaticCentralGravityTrajectory(
                            affectedBody,
                            centralBody,
                            request.deltaTime(),
                            request.steps()
                    )
                    .stream()
                    .map(simulationBodyMapper::toResponse)
                    .toList();

            trajectories.add(new BodyTrajectoryResponse(
                    i,
                    affectedBody.getMass(),
                    trajectory
            ));
        }

        return new GravityTrajectoryResponse(
                centralBodyIndex,
                simulationBodyMapper.toResponse(centralBody.getPosition()),
                trajectories
        );
    }
}