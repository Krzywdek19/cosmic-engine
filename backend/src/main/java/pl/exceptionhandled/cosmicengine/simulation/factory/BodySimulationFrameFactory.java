package pl.exceptionhandled.cosmicengine.simulation.factory;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

@Component
public class BodySimulationFrameFactory {

    public BodySimulationFrame create(
            int step,
            double time,
            Body body
    ) {
        return new BodySimulationFrame(
                step,
                time,
                body.getPosition(),
                body.getVelocity(),
                body.getAcceleration()
        );
    }
}