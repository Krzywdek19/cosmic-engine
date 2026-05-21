package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.BodyTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;
import pl.exceptionhandled.cosmicengine.simulation.policy.CentralBodySelectionPolicy;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationLoop simulationLoop;
    private final CentralBodySelectionPolicy centralBodySelectionPolicy;

    public GravityTrajectoryResponse simulateStaticCentralGravityTrajectory(GravityTrajectoryRequest request) {
        List<Body> bodies = request.bodies()
                .stream()
                .map(this::toBody)
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
                    .map(this::toResponse)
                    .toList();

            trajectories.add(new BodyTrajectoryResponse(
                    i,
                    affectedBody.getMass(),
                    trajectory
            ));
        }

        return new GravityTrajectoryResponse(
                centralBodyIndex,
                toResponse(centralBody.getPosition()),
                trajectories
        );
    }

    private Body toBody(SimulationBodyRequest request) {
        return new Body(
                request.mass(),
                toVector2D(request.position()),
                toVector2D(request.velocity()),
                new Vector2D(0.0, 0.0)
        );
    }

    private Vector2D toVector2D(Vector2DRequest request) {
        return new Vector2D(request.x(), request.y());
    }

    private Vector2DResponse toResponse(Vector2D vector) {
        return new Vector2DResponse(vector.x(), vector.y());
    }
}