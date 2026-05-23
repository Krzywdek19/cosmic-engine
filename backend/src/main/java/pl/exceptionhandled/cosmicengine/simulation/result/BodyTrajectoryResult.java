package pl.exceptionhandled.cosmicengine.simulation.result;

import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import java.util.List;

public record BodyTrajectoryResult(
        int bodyIndex,
        double mass,
        List<BodySimulationFrame> frames
) {
}