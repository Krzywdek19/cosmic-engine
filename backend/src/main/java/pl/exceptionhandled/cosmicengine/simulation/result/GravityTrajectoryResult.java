package pl.exceptionhandled.cosmicengine.simulation.result;

import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;

import java.util.List;

public record GravityTrajectoryResult(
        GravitySimulationModel model,
        IntegratorType integrator,
        int centralBodyIndex,
        Vector2D centralBodyPosition,
        List<BodyTrajectoryResult> trajectories
) {
}