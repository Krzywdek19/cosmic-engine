package pl.exceptionhandled.cosmicengine.physics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cosmic.physics")
public record PhysicsProperties(
        double gravitationalConstant
) {

    public PhysicsProperties {
        if (gravitationalConstant <= 0.0) {
            throw new IllegalArgumentException("Gravitational constant must be greater than zero");
        }
    }
}