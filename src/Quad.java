import java.awt.geom.Point2D;

class Quad {

    float x, y, length;
    Quad parent;
    Quad NW, NE, SE, SW;

    Body body;
    float totalMass = -1;
    Point2D.Float centerOfMass;

    Quad(float length) {
        this(0, 0, length, null);
    }

    Quad(float x, float y, float length, Quad parent) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.parent = parent;
    }

    void clear() {
        body = null;
        NW = null;
        NE = null;
        SW = null;
        SE = null;
    }

    boolean isLeaf() {
        return NW == null && NE == null && SE == null && SW == null;
    }

    Quad getNW() {
        return new Quad(x - length / 4, y + length / 4, length / 2, this);
    }

    Quad getNE() {
        return new Quad(x + length / 4, y + length / 4, length / 2, this);
    }

    Quad getSW() {
        return new Quad(x - length / 4, y - length / 4, length / 2, this);
    }

    Quad getSE() {
        return new Quad(x + length / 4, y - length / 4, length / 2, this);
    }

    float getTotalMass() {

        if (totalMass != -1)
            return totalMass;

        if (isLeaf())
            return body.mass;

        int m = 0;
        if (NW != null) m += NW.getTotalMass();
        if (NE != null) m += NE.getTotalMass();
        if (SW != null) m += SW.getTotalMass();
        if (SE != null) m += SE.getTotalMass();

        totalMass = m;
        return m;
    }

    Point2D.Float getCenterOfMass() {

        if (centerOfMass != null)
            return centerOfMass;

        if (isLeaf()) {
            return new Point2D.Float(body.px, body.py);
        }

        Point2D nwCenter, neCenter, swCenter, seCenter;
        float nwMass, neMass, swMass, seMass;

        float x = 0, y = 0;
        float totalMass = 0;

        if (NW != null) {

            nwCenter = NW.getCenterOfMass();
            nwMass = NW.getTotalMass();

            x += nwCenter.getX() * nwMass;
            y += nwCenter.getY() * nwMass;
            totalMass += nwMass;
        }
        if (NE != null){

            neCenter = NE.getCenterOfMass();
            neMass = NE.getTotalMass();

            x += neCenter.getX() * neMass;
            y += neCenter.getY() * neMass;
            totalMass += neMass;
        }
        if (SW != null) {

            swCenter = SW.getCenterOfMass();
            swMass = SW.getTotalMass();

            x += swCenter.getX() * swMass;
            y += swCenter.getY() * swMass;
            totalMass += swMass;
        }
        if (SE != null) {

            seCenter = SE.getCenterOfMass();
            seMass = SE.getTotalMass();

            x += seCenter.getX() * seMass;
            y += seCenter.getY() * seMass;
            totalMass += seMass;
        }

        x /= totalMass;
        y /= totalMass;

        centerOfMass = new Point2D.Float(x, y);
        return centerOfMass;
    }

    int count() {

        if (isLeaf())
            return 1;

        int c = 0;
        if (NW != null) c += NW.count();
        if (NE != null) c += NE.count();
        if (SW != null) c += SW.count();
        if (SE != null) c += SE.count();

        return c;
    }

    boolean contains(Body b) {

        float l = length / 2;

        return b.px <= x + l
                && b.px >= x - l
                && b.py <= y + l
                && b.py >= y - l;
    }

    void insert(Body b) {

        //Empty quad -> add body as quad's data
        if (isLeaf() && body == null) {
            body = b;
        }

        //Quad has data but is a leaf -> insert b and current body into correct sub-quads
        else if (isLeaf()) {

            Quad nw = getNW();
            if (nw.contains(b)) {
                nw.insert(b);
                NW = nw;
                addCurrentBody();
                return;
            }

            Quad ne = getNE();
            if (ne.contains(b)) {
                ne.insert(b);
                NE = ne;
                addCurrentBody();
                return;
            }

            Quad sw = getSW();
            if (sw.contains(b)) {
                sw.insert(b);
                SW = sw;
                addCurrentBody();
                return;
            }

            Quad se = getSE();
            if (se.contains(b)) {
                se.insert(b);
                SE = se;
                addCurrentBody();
                return;
            }
        }

        //Quad is not a leaf -> insert into correct sub-quad
        else if (!isLeaf()) {

            if (NW == null) {
                Quad nw = getNW();
                if (nw.contains(b)) {
                    nw.insert(b);
                    NW = nw;
                }
            }
            else {
                if (NW.contains(b)) NW.insert(b);
            }

            if (NE == null) {
                Quad ne = getNE();
                if (ne.contains(b)) {
                    ne.insert(b);
                    NE = ne;
                }
            }
            else {
                if (NE.contains(b)) NE.insert(b);
            }

            if (SW == null) {
                Quad sw = getSW();
                if (sw.contains(b)) {
                    sw.insert(b);
                    SW = sw;
                }
            }
            else {
                if (SW.contains(b)) SW.insert(b);
            }

            if (SE == null) {
                Quad se = getSE();
                if (se.contains(b)) {
                    se.insert(b);
                    SE = se;
                }
            }
            else {
                if (SE.contains(b)) SE.insert(b);
            }
        }
    }

    void addCurrentBody() {

        insert(new Body(body));
        body = null;
    }

    void updateForce(Body b) {

        if (isLeaf() && body != b) {
            b.addForce(b);
        }
        else {

            float d = b.distanceTo(getCenterOfMass());
            float t = length / d;

            if (t < .5) {

                b.addForce(getCenterOfMass(), getTotalMass());
            }
            else {

                if (NW != null) NW.updateForce(b);
                if (SW != null) SW.updateForce(b);
                if (SE != null) SE.updateForce(b);
                if (NE != null) NE.updateForce(b);
            }
        }
    }
}