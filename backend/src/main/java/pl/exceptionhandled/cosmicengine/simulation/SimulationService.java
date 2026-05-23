package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;
import pl.exceptionhandled.cosmicengine.simulation.policy.CentralBodySelectionPolicy;
import pl.exceptionhandled.cosmicengine.simulation.result.BodyTrajectoryResult;
import pl.exceptionhandled.cosmicengine.simulation.result.GravityTrajectoryResult;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationLoop simulationLoop;
    private final CentralBodySelectionPolicy centralBodySelectionPolicy;

    public GravityTrajectoryResult simulateStaticCentralGravityTrajectory(GravityTrajectoryCommand command) {
        List<Body> bodies = command.bodies();

        int centralBodyIndex = centralBodySelectionPolicy.selectCentralBodyIndex(bodies);

        Body centralBody = bodies.get(centralBodyIndex);

        List<BodyTrajectoryResult> trajectories = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            if (i == centralBodyIndex) {
                continue;
            }

            Body affectedBody = bodies.get(i);

            List<BodySimulationFrame> frames = simulationLoop.runStaticCentralGravityFrames(
                    affectedBody,
                    centralBody,
                    command.deltaTime(),
                    command.steps()
            );

            trajectories.add(new BodyTrajectoryResult(
                    i,
                    affectedBody.getMass(),
                    frames
            ));
        }

        return new GravityTrajectoryResult(
                GravitySimulationModel.STATIC_CENTRAL_BODY,
                IntegratorType.CONSTANT_ACCELERATION_STEP,
                centralBodyIndex,
                centralBody.getPosition(),
                trajectories
        );
    }
}