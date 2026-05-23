package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.factory.BodySimulationFrameFactory;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SimulationLoop {

    private final PhysicsEngine physicsEngine;
    private final NewtonianGravityCalculator gravityCalculator;
    private final BodySimulationFrameFactory bodySimulationFrameFactory;

    public void run(Body body, double deltaTime, int steps) {
        validateSteps(steps);

        for (int i = 0; i < steps; i++) {
            physicsEngine.update(body, deltaTime);
        }
    }

    public void runStaticCentralGravity(
            Body affectedBody,
            Body centralBody,
            double deltaTime,
            int steps
    ) {
        validateSteps(steps);

        for (int i = 0; i < steps; i++) {
            Vector2D gravityForce = gravityCalculator.calculateForce(affectedBody, centralBody);

            physicsEngine.applyForces(affectedBody, List.of(gravityForce));
            physicsEngine.update(affectedBody, deltaTime);
        }
    }

    public List<BodySimulationFrame> runStaticCentralGravityFrames(
            Body affectedBody,
            Body centralBody,
            double deltaTime,
            int steps
    ) {
        validateSteps(steps);

        List<BodySimulationFrame> frames = new ArrayList<>();
        frames.add(bodySimulationFrameFactory.create(0, 0.0, affectedBody));

        for (int i = 1; i <= steps; i++) {
            Vector2D gravityForce = gravityCalculator.calculateForce(affectedBody, centralBody);

            physicsEngine.applyForces(affectedBody, List.of(gravityForce));
            physicsEngine.update(affectedBody, deltaTime);

            frames.add(bodySimulationFrameFactory.create(i, i * deltaTime, affectedBody));
        }

        return frames;
    }

    private void validateSteps(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be greater than zero");
        }
    }
}