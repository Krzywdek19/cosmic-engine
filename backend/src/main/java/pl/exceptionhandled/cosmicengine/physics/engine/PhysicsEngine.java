package pl.exceptionhandled.cosmicengine.physics.engine;

import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

public class PhysicsEngine {

    public void update(Body body, double deltaTime) {
        if (deltaTime <= 0) {
            throw new IllegalArgumentException("Delta time must be greater than zero");
        }

        Vector2D oldPosition = body.getPosition();
        Vector2D oldVelocity = body.getVelocity();
        Vector2D acceleration = body.getAcceleration();

        Vector2D newPosition = oldPosition
                .add(oldVelocity.multiply(deltaTime))
                .add(acceleration.multiply(0.5 * deltaTime * deltaTime));

        Vector2D newVelocity = oldVelocity
                .add(acceleration.multiply(deltaTime));

        body.setPosition(newPosition);
        body.setVelocity(newVelocity);
    }
}