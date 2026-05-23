package pl.exceptionhandled.cosmicengine.physics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.config.PhysicsProperties;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

@Component
public class NewtonianGravityCalculator {

    private final double gravitationalConstant;
    private static final double MIN_DISTANCE = 1.0e-9;

    @Autowired
    public NewtonianGravityCalculator(PhysicsProperties physicsProperties) {
        this(physicsProperties.gravitationalConstant());
    }

    public NewtonianGravityCalculator(double gravitationalConstant) {
        if (gravitationalConstant <= 0.0) {
            throw new IllegalArgumentException("Gravitational constant must be greater than zero");
        }

        this.gravitationalConstant = gravitationalConstant;
    }

    public Vector2D calculateForce(Body affectedBody, Body attractingBody) {
        Vector2D direction = attractingBody.getPosition().subtract(affectedBody.getPosition());

        double distance = direction.magnitude();

        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("Distance between bodies is too small");
        }

        double forceMagnitude = gravitationalConstant
                * affectedBody.getMass()
                * attractingBody.getMass()
                / (distance * distance);

        return direction.normalize().multiply(forceMagnitude);
    }
}