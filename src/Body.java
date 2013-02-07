import java.awt.geom.Point2D;

final class Body {

    private static final double G = 6.673e-11;

    float px, py;
    float vx, vy;
    float fx, fy;
    float mass;

    public Body(float px, float py, float vx, float vy, float mass) {

        this.px = px;
        this.py = py;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
    }

    public Body(Body b) {
        this(b.px, b.py, b.vx, b.vy, b.mass);
    }

    public float distanceTo(Body b) {

        float dx = px - b.px;
        float dy = py - b.py;
        return (float) Math.sqrt(dx*dx + dy*dy);
    }

    public float distanceTo(Point2D.Float p) {

        float dx = px - p.x;
        float dy = py - p.y;
        return (float) Math.sqrt(dx*dx + dy*dy);
    }

    public void addForce(Body b) {

        Body a = this;
        double EPS = 3E4;
        double dx = b.px - a.px;
        double dy = b.py - a.py;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double F = (G * a.mass * b.mass) / (dist*dist + EPS*EPS);
        a.fx += F * dx / dist;
        a.fy += F * dy / dist;
    }

    public void addForce(Point2D.Float p, float mass) {

        Body a = this;
        double EPS = 3E4;
        double dx = p.x - a.px;
        double dy = p.y - a.py;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double F = (G * a.mass * mass) / (dist*dist + EPS*EPS);
        a.fx += F * dx / dist;
        a.fy += F * dy / dist;
    }

    public void update(float dt) {

        vx += dt * fx / mass;
        vy += dt * fy / mass;
        px += dt * vx;
        py += dt * vy;
    }

    public void resetForces() {
        fx = 0;
        fy = 0;
    }

    @Override
    public String toString() {
        return px + ", " + py + " - " + mass;
    }
}