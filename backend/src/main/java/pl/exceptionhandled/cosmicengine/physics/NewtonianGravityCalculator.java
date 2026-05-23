package pl.exceptionhandled.cosmicengine.physics;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

@Component
public class NewtonianGravityCalculator {
    private final double gravitationalConstant;

    public NewtonianGravityCalculator(double gravitationalConstant) {
        this.gravitationalConstant = gravitationalConstant;
    }

    public NewtonianGravityCalculator() {
        this.gravitationalConstant = 6.67430 * Math.pow(10, -11);
    }

    public Vector2D calculateForce(Body affectedBody, Body attractingBody) {
        Vector2D distanceVector = attractingBody.getPosition()
                .subtract(affectedBody.getPosition());

        double distance = distanceVector.magnitude();

        if (distance == 0) {
            throw new IllegalArgumentException("Distance between bodies cannot be zero");
        }

        Vector2D direction = distanceVector.normalize();

        double forceMagnitude = gravitationalConstant
                * affectedBody.getMass()
                * attractingBody.getMass()
                / Math.pow(distance, 2);

        return direction.multiply(forceMagnitude);
    }
}