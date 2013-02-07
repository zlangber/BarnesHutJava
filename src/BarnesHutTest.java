public class BarnesHutTest {

    final float solarMass = 1.98892e30f;

    Body[] bodies;

    public BarnesHutTest(int n, int s, boolean doPP) {

        bodies = new Body[n];

        for (int i = 0; i < n; i++) {
            bodies[i] = generateBody();
        }

        /*
            BH Run
         */
        long start = System.nanoTime();
        for (int i = 0; i < s; i++) {
            step();
        }
        System.out.println("Total time (BH): " + ((System.nanoTime() - start) / 1000000) + "ms");

        /*
            PP Run
         */
        if (!doPP) return;
        start = System.nanoTime();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    bodies[j].addForce(bodies[k]);
                }
                bodies[j].update(1e11f);
            }
        }
        System.out.println("Total time (PP): " + ((System.nanoTime() - start) / 1000000) + "ms");
    }

    void step() {

        Quad tree = new Quad(2 * 1e18f);

        for (int i = 0; i < bodies.length; i++) {
            tree.insert(bodies[i]);
        }

        for (int i = 0; i < bodies.length; i++) {
            tree.updateForce(bodies[i]);
            bodies[i].update(1e11f);
        }
    }

    Body generateBody() {

        float px = (float) (1e18 * Math.exp(-1.8) * (.5 - Math.random()));
        float py = (float) (1e18 * Math.exp(-1.8) * (.5 - Math.random()));
        float magv = circlev(px, py);

        float absangle = (float) Math.atan(Math.abs(py/px));
        float thetav = (float) Math.PI / 2 - absangle;

        float vx = (float) (-1 * Math.signum(py) * Math.cos(thetav) * magv);
        float vy = (float) (Math.signum(px) * Math.sin(thetav) * magv);

        if (Math.random() <= .5) {
            vx = -vx;
            vy = -vy;
        }

        float mass = (float) (Math.random() * solarMass * 10+1e20);

        return new Body(px, py, vx, vy, mass);
    }

    float circlev(float rx, float ry)  {

        float a = (float) Math.sqrt(rx * rx + ry * ry);
        float b = (float) ((6.67e-11) * 1e6 * solarMass);
        return (float) Math.sqrt(b / a);
    }

    public static void main(String[] args) {

        int numParticles = 1000000;
        int steps = 1;
        new BarnesHutTest(numParticles, steps, true);
    }
}
