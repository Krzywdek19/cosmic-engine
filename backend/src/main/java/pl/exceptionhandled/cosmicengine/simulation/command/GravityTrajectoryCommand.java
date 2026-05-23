package pl.exceptionhandled.cosmicengine.simulation.command;

import pl.exceptionhandled.cosmicengine.physics.model.Body;

import java.util.List;

public record GravityTrajectoryCommand(
        List<Body> bodies,
        double deltaTime,
        int steps
) {
}