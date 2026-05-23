package pl.exceptionhandled.cosmicengine.physics.model;

import lombok.Getter;

@Getter
public class Body {

    private final double mass;

    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;

    public Body(double mass, Vector2D position, Vector2D velocity, Vector2D acceleration) {
        validateMass(mass);

        this.mass = mass;
        setPosition(position);
        setVelocity(velocity);
        setAcceleration(acceleration);
    }

    public void setPosition(Vector2D position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        this.position = position;
    }

    public void setVelocity(Vector2D velocity) {
        if (velocity == null) {
            throw new IllegalArgumentException("Velocity cannot be null");
        }

        this.velocity = velocity;
    }

    public void setAcceleration(Vector2D acceleration) {
        if (acceleration == null) {
            throw new IllegalArgumentException("Acceleration cannot be null");
        }

        this.acceleration = acceleration;
    }

    private void validateMass(double mass) {
        if (!Double.isFinite(mass)) {
            throw new IllegalArgumentException("Mass must be a finite number");
        }

        if (mass <= 0.0) {
            throw new IllegalArgumentException("Mass must be greater than zero");
        }
    }
}