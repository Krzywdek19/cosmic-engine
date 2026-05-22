package pl.exceptionhandled.cosmicengine.physics.integrator;

import pl.exceptionhandled.cosmicengine.physics.model.Body;

public interface MotionIntegrator {
    Body integrate(Body body, double deltaTime);
}
