package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import pl.exceptionhandled.cosmicengine.simulation.model.GravitySimulationModel;
import pl.exceptionhandled.cosmicengine.simulation.model.IntegratorType;

import java.util.List;

public record GravityTrajectoryResponse(
        GravitySimulationModel model,
        IntegratorType integrator,
        int centralBodyIndex,
        Vector2DResponse centralBodyPosition,
        List<BodyTrajectoryResponse> trajectories
) {
}