package pl.exceptionhandled.cosmicengine.physics.integrator;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

@Component
public class ConstantAccelerationStepIntegrator implements MotionIntegrator {

    @Override
    public Body integrate(Body body, double deltaTime) {
        validateDeltaTime(deltaTime);

        Vector2D newPosition = body.getPosition()
                .add(body.getVelocity().multiply(deltaTime))
                .add(body.getAcceleration().multiply(0.5 * deltaTime * deltaTime));

        Vector2D newVelocity = body.getVelocity()
                .add(body.getAcceleration().multiply(deltaTime));

        body.setPosition(newPosition);
        body.setVelocity(newVelocity);

        return body;
    }

    private void validateDeltaTime(double deltaTime) {
        if (deltaTime <= 0.0) {
            throw new IllegalArgumentException("Delta time must be greater than zero");
        }
    }
}