package pl.exceptionhandled.cosmicengine.physics.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.integrator.MotionIntegrator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PhysicsEngine {

    private final MotionIntegrator motionIntegrator;

    public Body update(Body body, double deltaTime) {
        return motionIntegrator.integrate(body, deltaTime);
    }

    public void applyForces(Body body, List<Vector2D> forces) {
        Vector2D totalForce = forces.stream()
                .reduce(Vector2D.ZERO, Vector2D::add);

        applyTotalForce(body, totalForce);
    }

    public void applyTotalForce(Body body, Vector2D totalForce) {
        Vector2D acceleration = totalForce.divide(body.getMass());
        body.setAcceleration(acceleration);
    }
}