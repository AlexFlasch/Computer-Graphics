import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.Random;

public class ReflectedObject implements Cloneable {

    // a ReflectedObject takes into account the 0,0 of a viewport,
    // assuming the viewport is square, and they are all the same size
    // using these assumptions, we can translate the vertices for this object
    // into drawable objects in each of the quadrants.

    public ArrayList<Point> vertices;
    public KShape shapeType;
    public float[] colorRGB;

    public double width;
    public double height;

    public static final double DEG2RAD = 3.14159/180;
    public static final int NUM = 3000;

    /**
     * Constructor, nothing special here.
     *
     * @param points The vertices of the shape.
     * @param shape The shapeType of the shape (Triangle, Circle, Rectangle, Epicycloid)
     * @param color A 3 float array for R, G, and B, respectively.
     */
    public ReflectedObject(ArrayList<Point> points, KShape shape, float[] color) {
        this.vertices = points;
        shapeType = shape;
        colorRGB = color;
    }

    /**
     * creates a new instance of ReflectedObject in order to generate new shapes on click
     *
     * @param x the x coordinate that was clicked
     * @param y the y coordinate that was clicked
     * @param color the shape's color
     * @return an instance of the newly generated ReflectedObject
     */
    public static ReflectedObject generateShapeAtCoords(double x, double y, float[] color) {
        Random r = new Random();

        int shape = r.nextInt(4);
        int size = r.nextInt(15) + 5;

        ArrayList<Point> vertices = new ArrayList<>();

        ReflectedObject temp;

        double wcWidth = Assignment2.wcCoords.get("right") - Assignment2.wcCoords.get("left");
        double wcHeight = Assignment2.wcCoords.get("top") - Assignment2.wcCoords.get("bottom");

        int quadrantWidth = (int) wcWidth/2;
        int quadrantHeight = (int) wcHeight/2;

        switch(shape) {
            case 0:
                vertices.add(new Point(x, y));

                // generate triangle vertices
                for(int i = 0; i < 2; i++) {
                    int tempX = r.nextInt(quadrantWidth);
                    int tempY = r.nextInt(quadrantHeight);

                    vertices.add(new Point(tempX, tempY));
                }

                temp = new ReflectedObject(vertices, KShape.TRIANGLE, color);
                break;

            case 1:
                // generate circle vertices
                int startingX = (int) x;
                int startingY = (int) y;

                vertices = circle(size, startingX, startingY);

                temp = new ReflectedObject(vertices, KShape.CIRCLE, color);
                break;

            case 2:
                // generate quad vertices
                int width = r.nextInt(quadrantWidth) + 1;
                int height = r.nextInt(quadrantHeight) + 1;
                startingX = (int) x;
                startingY = (int) y;
                vertices = rect(width, height, startingX, startingY);

                temp = new ReflectedObject(vertices, KShape.QUAD, color);
                break;

            case 3:
                // add butterfly to ReflectedObjects list
                double a = 5.0/3.0;
                double b = 7.0/6.0;
                vertices = epicycloid(size, a, b, (int) x, (int) y);

                temp = new ReflectedObject(vertices, KShape.EPICYCLOID, color);
                break;

            default:
                throw new Error("Uhh... This shouldn't happen.");
        }

        return temp;
    }

    /**
     * Since every shape has vertices with points somewhere between 0 and whatever the width of the quadrant is
     * these points must be translated over to draw into all the quadrants.
     *
     * @param quadrant The quadrant to grab the vertices for (1-4, following the same form as cartesian quadrants).
     * @return A list of vertices that tells OpenGL how to draw the shape in the specified quadrant.
     */
    private ArrayList<Point> getPointsForQuadrant(int quadrant) {
        ArrayList<Point> translatedPoints = new ArrayList<>();

        switch(quadrant) {
            case 1:
                // calculate vertices for quadrant 1 (top right)
                for(Point vert : vertices) {
                    double translatedX = vert.x;
                    double translatedY = vert.y;

                    translatedPoints.add(new Point(translatedX, translatedY));
                }
                break;

            case 2:
                // calculate vertices for quadrant 2 (top left)
                for(Point vert : vertices) {
                    double translatedX = -1 * vert.x;
                    double translatedY = vert.y;

                    translatedPoints.add(new Point(translatedX, translatedY));
                }
                break;

            case 3:
                // calculate vertices for quadrant 3 (bottom left)
                for(Point vert : vertices) {
                    double translatedX = -1 * vert.x;
                    double translatedY = -1 * vert.y;

                    translatedPoints.add(new Point(translatedX, translatedY));
                }
                break;

            case 4:
                // calculate vertices for quadrant 4 (bottom right)
                for(Point vert : getVertices()) {
                    double translatedX = vert.x;
                    double translatedY = -1 * vert.y;

                    translatedPoints.add(new Point(translatedX, translatedY));
                }
                break;

            default:
                throw new Error("Uhhh that's not supposed to happen.");
        }

        return translatedPoints;
    }

    /**
     * This method will draw a shape in each of the 4 quadrants automagically.
     * This allows the shapes to be relatively easy to handle in the sense that having "1 shape" will let
     * you draw all 4 that you really need.
     *
     * @param gl The JOGL GL2 object.
     */
    public void draw(GL2 gl) {
        int glShape;

        switch(shapeType) {
            case TRIANGLE:
                glShape = GL2.GL_TRIANGLES;
                break;
            case CIRCLE:
                glShape = GL2.GL_TRIANGLE_FAN;
                break;
            case QUAD:
                glShape = GL2.GL_QUADS;
                break;
            case EPICYCLOID:
                glShape = GL2.GL_LINE_LOOP;
                break;
            default:
                throw new Error("Uhh... This shouldn't happen.");
        }

        for(int i = 1; i <= 4; i++) {
            ArrayList<Point> translatedPoints = getPointsForQuadrant(i);

            gl.glBegin(glShape);
            gl.glColor3f(colorRGB[0], colorRGB[1], colorRGB[2]);
            for(Point vert : translatedPoints) {
                gl.glVertex2d(vert.x, vert.y);
            }
            gl.glEnd();
        }
    }

    /**
     * Generates the vertices used to draw an epicycloid using a parameterized curve and the parameters passed into it.
     *
     * @param size This variable determines the size of the entire shape being drawn.
     * @param a This is (supposed to be) the radius of the inner circle in which the outer circle rotates around.
     * @param b This is (supposed to be) the radius of the outer circle that rotates around the inner circle.
     * @param startX This determines the x point on the screen that the vertices are generated at.
     * @param startY This determines the y point on the screen that the vertices are generated at.
     * @return A list of Point objects (Made myself, mostly just container objects) that is used to draw in OpenGL.
     */
    public static ArrayList<Point> epicycloid(int size, double a, double b, int startX, int startY) {
        final int POINTS = 3000;

        ArrayList<Point> points = new ArrayList<>();

        for(int i = 0; i < (100 * Math.PI); i++) {
            double x = startX + (size * ((a + b) * Math.cos(i) - b * Math.cos((a / b + 1) * i)));
            double y = startY + (size * ((a + b) * Math.sin(i) - b * Math.sin((a / b + 1) * i)));

            points.add(new Point(x, y));
        }

        return points;
    }

    /**
     * Generates the vertices used to draw a circle using a parameterized curve and a triangle fan to make a filled in circle.
     *
     * @param size This variable determines the size of the circle.
     * @param startX This determines the x point on the screen that the vertices are generated at.
     * @param startY This determines the y point on the screen that the vertices are generated at.
     * @return A list of Point objects that is used to tell OpenGL how to draw the circle
     */
    public static ArrayList<Point> circle(int size, int startX, int startY) {
        ArrayList<Point> points = new ArrayList<>();

        for(int i = 0; i < NUM; i++) {
            double degInRad = i * DEG2RAD;

            double x = startX + Math.cos(degInRad) * (size * 2);
            double y = startY + Math.sin(degInRad) * (size * 2);

            points.add(new Point(x, y));
        }

        return points;
    }

    /**
     * This method takes a width and a height and generates a rectangle of that width and height near
     * the specified x and y points
     *
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param startX The x location of the bottom left vertex.
     * @param startY The y location of the bottom left vertex.
     * @return A list of Point objects that tells OpenGL how to draw the rectangle.
     */
    public static ArrayList<Point> rect(int width, int height, int startX, int startY) {
        ArrayList<Point> points = new ArrayList<>();

        points.add(new Point(startX, startY)); // bottom left
        points.add(new Point(startX + width, startY)); // bottom right
        points.add(new Point(startX + width, startY + height)); // top right
        points.add(new Point(startX, startY + height)); // top left

        return points;
    }

    /**
     * Gets the shapeType field.
     *
     * @return The shapeType class field.
     */
    public KShape getShapeType() {
        return shapeType;
    }

    /**
     * Gets the colorRGB field.
     *
     * @return The colorRGB class field.
     */
    public float[] getColorRGB() {
        return colorRGB;
    }

    /**
     * Gets the vertices field.
     *
     * @return The vertices class field.
     */
    public ArrayList<Point> getVertices() {
        return vertices;
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double w) {
        this.width = w;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double h) {
        this.height = h;
    }
}