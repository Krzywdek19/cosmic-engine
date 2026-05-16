package pl.exceptionhandled.cosmicengine.simulation;

import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.engine.PhysicsEngine;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.ConfigurableSimpleMotionSimulationResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimpleMotionSimulationRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimpleMotionSimulationResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;

@Service
public class SimulationDemoService {

    private final SimulationLoop simulationLoop;
    private final PhysicsEngine physicsEngine;

    public SimulationDemoService(SimulationLoop simulationLoop, PhysicsEngine physicsEngine) {
        this.simulationLoop = simulationLoop;
        this.physicsEngine = physicsEngine;
    }

    public SimpleMotionSimulationResponse runSimpleMotionSimulation() {
        double deltaTime = 1.0;
        int steps = 5;

        Vector2D initialPosition = new Vector2D(0, 0);

        Body body = new Body(
                1,
                initialPosition,
                new Vector2D(10, 0),
                new Vector2D(0, 0)
        );

        simulationLoop.run(body, deltaTime, steps);

        return new SimpleMotionSimulationResponse(
                Vector2DResponse.from(initialPosition),
                Vector2DResponse.from(body.getPosition()),
                Vector2DResponse.from(body.getVelocity()),
                steps,
                deltaTime
        );
    }

    public ConfigurableSimpleMotionSimulationResponse runConfigurableSimpleMotionSimulation(
            SimpleMotionSimulationRequest request
    ) {
        Vector2D initialPosition = request.position().toVector2D();
        Vector2D initialVelocity = request.velocity().toVector2D();
        Vector2D appliedForce = request.force().toVector2D();

        Body body = new Body(
                request.mass(),
                initialPosition,
                initialVelocity,
                new Vector2D(0, 0)
        );

        physicsEngine.applyForce(body, appliedForce);
        simulationLoop.run(body, request.deltaTime(), request.steps());

        return new ConfigurableSimpleMotionSimulationResponse(
                body.getMass(),
                Vector2DResponse.from(initialPosition),
                Vector2DResponse.from(appliedForce),
                Vector2DResponse.from(body.getAcceleration()),
                Vector2DResponse.from(body.getPosition()),
                Vector2DResponse.from(body.getVelocity()),
                request.steps(),
                request.deltaTime()
        );
    }
}
