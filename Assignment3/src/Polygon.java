import com.jogamp.opengl.GL2;

import java.util.LinkedList;

/**
 * Created by alexanderflasch on 3/5/16.
 */
public class Polygon {
    public LinkedList<Point> vertices;

    public double budgeX = 0.0;
    public double budgeY = 0.0;
    public double rotation = 0.0;
    public double scaleX = 1.0;
    public double scaleY = 1.0;

    public boolean isCurrent;

    public double width;
    public double height;

    public double centerX;
    public double centerY;

    public double[] currTransMatrix;

    public boolean finalized = false;

    public Polygon() {
        vertices = new LinkedList<>();
    }

    public static Polygon copyPolygon(Polygon p) {
        Polygon temp = new Polygon();

        temp.budgeX = p.getBudgeX();
        temp.budgeY = p.getBudgeY();
        temp.rotation = p.getRotation();
        temp.scaleX = p.getScaleX();
        temp.scaleY = p.getScaleY();
        temp.width = p.getWidth();
        temp.height = p.getHeight();
        temp.centerX = p.getCenterX();
        temp.centerY = p.getCenterY();
        temp.vertices = p.getVertices();
        temp.finalized = true;

        return temp;
    }

    public LinkedList<Point> getVertices() {
        return vertices;
    }

    public double getBudgeX() {
        return budgeX;
    }

    public double getBudgeY() {
        return budgeY;
    }

    public double getRotation() {
        return rotation;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void createPoint(double x, double y) {
        vertices.add(new Point(x, y));
    }

    public void clearPoints() {
        vertices.clear();
        finalized = false;
    }

    public void finalizePolygon() {
        finalized = true;
        calculateBounds();
    }

    public void calculateBounds() {
        double left = 999, down = 999;
        double right = -999, up = -999;
        for(Point v : vertices) {
            double x = v.getX();
            double y = v.getY();
            if(x < left) {
                left = x;
            }
            if(x > right) {
                right = x;
            }
            if(y < down) {
                down = y;
            }
            if(y > up) {
                up = y;
            }
        }

        width = Math.abs(right - left);
        height = Math.abs(up - down);

        centerX = right - (width / 2);
        centerY = up - (height / 2);
    }

    public void budge(double x, double y) {
        budgeX += x;
        budgeY += y;
    }

    public void rotate(double deg) {
        rotation += deg;
    }

    public void scale(double x, double y) {
        scaleX += x;
        scaleY += y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void draw(GL2 gl, float[] color) {
        currTransMatrix = new double[16];

        gl.glPushMatrix();
        gl.glTranslated(budgeX, budgeY, 0);
        gl.glTranslated(centerX, centerY, 0);
        gl.glScaled(scaleX, scaleY, 1.0);
        gl.glTranslated(-centerX, -centerY, 0);
        gl.glTranslated(centerX, centerY, 0);
        gl.glRotated(rotation, 0, 0, 1.0);
        gl.glTranslated(-centerX, -centerY, 0);

        // load current modelview matrix for this transformation
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, currTransMatrix, 0);

        if(finalized) {
            if(color == null) {
                if(isCurrent) {
                    gl.glColor3f(0.75f, 0.75f, 0.75f);
                }
                else {
                    gl.glColor3f(1.0f, 1.0f, 1.0f);
                }
            }
            else {
                gl.glColor3f(color[0], color[1], color[2]);
            }

            gl.glBegin(GL2.GL_POLYGON);
            for(Point v : vertices) {
                gl.glVertex2d(v.getX(), v.getY());
            }
            gl.glEnd();
        }
        else {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL2.GL_LINE_LOOP);
            for(Point v : vertices) {
                double x = v.getX();
                double y = v.getY();
                gl.glVertex2d(v.getX(), v.getY());
            }
            gl.glEnd();
        }
        gl.glPopMatrix();
    }

    public LinkedList<Polygon> getChildren(LinkedList<Polygon> seedChildren) {
        LinkedList<Polygon> currentChildren = new LinkedList<>();

        for(Polygon p : seedChildren) {
            Polygon temp = copyPolygon(p);

            temp.budgeX += p.budgeX;
            temp.budgeY += p.budgeY;
            temp.rotation += p.rotation;
            temp.scaleX += p.scaleX;
            temp.scaleY += p.scaleY;

            currentChildren.add(temp);
        }

        return currentChildren;
    }
}

class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}