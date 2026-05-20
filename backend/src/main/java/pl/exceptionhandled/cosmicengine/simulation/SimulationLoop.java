package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;

@RequiredArgsConstructor
@Component
public class SimulationLoop {

    private final PhysicsEngine physicsEngine;

    public void run(Body body, double deltaTime, int steps) {
        validateSteps(steps);

        for (int i = 0; i < steps; i++) {
            physicsEngine.update(body, deltaTime);
        }
    }

    public void runWithGravity(
            Body affectedBody,
            Body attractingBody,
            double deltaTime,
            int steps
    ) {
        validateSteps(steps);

        for (int i = 0; i < steps; i++) {
            physicsEngine.updateWithGravity(affectedBody, attractingBody, deltaTime);
        }
    }

    private void validateSteps(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be greater than zero");
        }
    }
}