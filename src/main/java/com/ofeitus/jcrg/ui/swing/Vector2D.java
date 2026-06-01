package com.ofeitus.jcrg.ui.swing;

public record Vector2D(double x, double y) {

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
        return new Vector2D(this.x / scalar, this.y / scalar);
    }

    public Vector2D invert() {
        return new Vector2D(-this.x, -this.y);
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(this.x / mag, this.y / mag);
    }
}

