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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationLoop simulationLoop;

    public GravityTrajectoryResponse simulateStaticCentralGravityTrajectory(GravityTrajectoryRequest request) {
        List<SimulationBodyRequest> bodyRequests = request.bodies();

        int attractingBodyIndex = findMostMassiveBodyIndex(bodyRequests);
        SimulationBodyRequest attractingBodyRequest = bodyRequests.get(attractingBodyIndex);

        Body attractingBody = toBody(attractingBodyRequest);

        List<BodyTrajectoryResponse> trajectories = new ArrayList<>();

        for (int i = 0; i < bodyRequests.size(); i++) {
            if (i == attractingBodyIndex) {
                continue;
            }

            SimulationBodyRequest affectedBodyRequest = bodyRequests.get(i);

            Body affectedBody = toBody(affectedBodyRequest);

            List<Vector2DResponse> trajectory = simulationLoop.runStaticCentralGravityTrajectory(
                            affectedBody,
                            attractingBody,
                            request.deltaTime(),
                            request.steps()
                    )
                    .stream()
                    .map(this::toResponse)
                    .toList();

            trajectories.add(new BodyTrajectoryResponse(
                    i,
                    affectedBodyRequest.mass(),
                    trajectory
            ));
        }

        return new GravityTrajectoryResponse(
                attractingBodyIndex,
                toResponse(attractingBody.getPosition()),
                trajectories
        );
    }

    private int findMostMassiveBodyIndex(List<SimulationBodyRequest> bodies) {
        int mostMassiveBodyIndex = 0;
        double highestMass = bodies.get(0).mass();

        for (int i = 1; i < bodies.size(); i++) {
            double currentMass = bodies.get(i).mass();

            if (currentMass > highestMass) {
                highestMass = currentMass;
                mostMassiveBodyIndex = i;
            }
        }

        return mostMassiveBodyIndex;
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