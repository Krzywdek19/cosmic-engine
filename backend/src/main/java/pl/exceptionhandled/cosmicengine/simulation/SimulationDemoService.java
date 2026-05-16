package pl.exceptionhandled.cosmicengine.simulation;

import org.springframework.stereotype.Service;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimpleMotionSimulationResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DResponse;

@Service
public class SimulationDemoService {

    private final SimulationLoop simulationLoop;

    public SimulationDemoService(SimulationLoop simulationLoop) {
        this.simulationLoop = simulationLoop;
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
}