package pl.exceptionhandled.cosmicengine.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.NewtonianGravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SimulationLoop {

    private final PhysicsEngine physicsEngine;
    private final NewtonianGravityCalculator newtonianGravityCalculator;

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
            Vector2D gravityForce = newtonianGravityCalculator.calculateForce(affectedBody, centralBody);

            physicsEngine.applyForces(affectedBody, List.of(gravityForce));
            physicsEngine.update(affectedBody, deltaTime);
        }
    }

    public List<Vector2D> runStaticCentralGravityTrajectory(
            Body affectedBody,
            Body centralBody,
            double deltaTime,
            int steps
    ) {
        validateSteps(steps);

        List<Vector2D> trajectory = new ArrayList<>();
        trajectory.add(affectedBody.getPosition());

        for (int i = 0; i < steps; i++) {
            Vector2D gravityForce = newtonianGravityCalculator.calculateForce(affectedBody, centralBody);

            physicsEngine.applyForces(affectedBody, List.of(gravityForce));
            physicsEngine.update(affectedBody, deltaTime);

            trajectory.add(affectedBody.getPosition());
        }

        return trajectory;
    }

    private void validateSteps(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be greater than zero");
        }
    }
}