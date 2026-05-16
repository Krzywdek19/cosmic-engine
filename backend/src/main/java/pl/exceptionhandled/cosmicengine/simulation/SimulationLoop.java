package pl.exceptionhandled.cosmicengine.simulation;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;

@Component
public class SimulationLoop {
    private final PhysicsEngine physicsEngine;

    public SimulationLoop(PhysicsEngine physicsEngine) {
        this.physicsEngine = physicsEngine;
    }

    public void run(Body body, double deltaTime, int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be greater than zero");
        }

        for (int i = 0; i < steps; i++) {
            physicsEngine.update(body, deltaTime);
        }
    }
}