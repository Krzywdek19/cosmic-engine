package pl.exceptionhandled.cosmicengine.physics.model;

public record Vector2D(double x, double y) {
    public static final Vector2D ZERO = new Vector2D(0.0, 0.0);

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public Vector2D divide(double scalar) {
        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide vector by zero");
        }
        return new Vector2D(this.x / scalar, this.y / scalar);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double length = magnitude();

        if (length == 0) {
            return new Vector2D(0, 0);
        }

        return this.divide(length);
    }
}