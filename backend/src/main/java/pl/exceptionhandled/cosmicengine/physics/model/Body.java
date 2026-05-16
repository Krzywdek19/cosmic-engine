package pl.exceptionhandled.cosmicengine.physics.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Body {

    private final double mass;

    @Setter
    private Vector2D position;

    @Setter
    private Vector2D velocity;

    @Setter
    private Vector2D acceleration;

    public Body(double mass, Vector2D position, Vector2D velocity, Vector2D acceleration) {
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass must be greater than zero");
        }

        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }
}