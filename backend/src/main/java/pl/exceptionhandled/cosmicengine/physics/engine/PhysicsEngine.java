package pl.exceptionhandled.cosmicengine.physics.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.GravityCalculator;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PhysicsEngine {

    private final GravityCalculator gravityCalculator;

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

    public void applyTotalForce(Body body, Vector2D force) {
        Vector2D acceleration = force.divide(body.getMass());
        body.setAcceleration(acceleration);
    }

    public void applyForces(Body body, List<Vector2D> forces) {
        Vector2D totalAcceleration = forces
                .stream()
                .reduce(Vector2D.ZERO, Vector2D::add);

        applyTotalForce(body, totalAcceleration);
    }

    public Body updateInStaticGravityField(
            Body affectedBody,
            Body attractingBody,
            double deltaTime
    ) {
        Vector2D gravityForce = gravityCalculator.calculateForce(affectedBody, attractingBody);

        applyForces(affectedBody, List.of(gravityForce));

        update(affectedBody, deltaTime);

        return affectedBody;
    }
}