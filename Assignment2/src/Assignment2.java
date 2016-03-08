import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*
 * Some extra features:
 *      * generates random shapes at your click destination
 *      * the zoom is animated, and eased with a cubic function
 */

public class Assignment2 extends JFrame implements GLEventListener, KeyListener, MouseListener {

    public static int windowWidth = 800;
    public static int windowHeight = 800;

    public static HashMap<String, Double> wcCoords;

    public boolean zoomed = true;
    public boolean changeZoom = false;
    public boolean currentlyZooming = false;

    public double t = 0;

    public int viewportsX;
    public int viewportsY;

    private static GLU glu;
    private static GL2 gl;
    private static GLCapabilities capabilities;
    private static FPSAnimator fps;

    public Color bg = Color.decode("#2C3E50");
    public ArrayList<Color> palette;

    public ArrayList<ReflectedObject> reflectedObjects = new ArrayList<>();

    /**
     * Constructor: doesn't do a whole ton aside from define the palette.
     */
    public Assignment2() {
        palette = new ArrayList<>();
        // take hex values and turn them into java Color objects :)
        palette.add(Color.decode("#22A7F0"));
        palette.add(Color.decode("#2ECC71"));
        palette.add(Color.decode("#F27935"));
        palette.add(Color.decode("#8E44AD"));
        palette.add(Color.decode("#D64541"));
        palette.add(Color.decode("#F7CA18"));
        palette.add(Color.decode("#F62459"));

        wcCoords = new HashMap<>();
        wcCoords.put("left", 0.0);
        wcCoords.put("right", 100.0);
        wcCoords.put("bottom", 0.0);
        wcCoords.put("top", 100.0);
    }

    /**
     * Main method which gets everything set up for rendering.
     *
     * @param args Accepts 2 parameters:
     *             first defines the amount of viewports in the x direction
     *             second defines the amount of viewports in the y direction
     */
    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();
        Assignment2 kaleidoscope = new Assignment2();

        capabilities = new GLCapabilities(GLProfile.getGL2GL3());
        capabilities.setDoubleBuffered(true);
        capabilities.setHardwareAccelerated(true);

        fps = new FPSAnimator(canvas, 60);

        int arg1 = 3;
        int arg2 = 3;

        if(args.length != 0) {
            arg1 = Integer.parseInt(args[0]);
            arg2 = Integer.parseInt(args[1]);
        }

        kaleidoscope.viewportsX = arg1;
        kaleidoscope.viewportsY = arg2;

        canvas.addGLEventListener(kaleidoscope);
        JFrame frame = new JFrame("Assignment2");
        frame.setSize(kaleidoscope.windowWidth, kaleidoscope.windowHeight);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();

        canvas.addMouseListener(kaleidoscope);
        canvas.addKeyListener(kaleidoscope);
    }

    /**
     *  init sets a few things up. Among many things, it adds antialiasing for pretty looking shapes,
     *  sets the background color, calculates what the width and height of the viewports should be,
     *  and lastly sets the gluOrtho2D.
     *
     * @param drawable JOGL drawable object
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
        glu = new GLU();

        // make things pretty and antialias everything :)
        if (gl.isExtensionAvailable("GL_ARB_multisample")) {
            gl.glEnable(GL.GL_MULTISAMPLE);
        }

        float[] bgArr = getGLColorArray(bg);
        float r = bgArr[0], g = bgArr[1], b = bgArr[2];
        gl.glClearColor(r, g, b, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glFlush();

        HashMap<String, Double> map = getWorldCoordinates();
        double left = map.get("left");
        double right = map.get("right");
        double bottom = map.get("bottom");
        double top = map.get("top");

        glu.gluOrtho2D(left, right, bottom, top);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        fps.start();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    /**
     * Runs the methods necessary to draw everything to the screen/viewports!
     *
     * @param glAutoDrawable JOGL GLAutoDrawable object
     */
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        gl = glAutoDrawable.getGL().getGL2();

        gl.glFlush();

        double left = wcCoords.get("left");
        double right = wcCoords.get("right");
        double bottom = wcCoords.get("bottom");
        double top = wcCoords.get("top");

        // animate zooming
        if(currentlyZooming) {
            t++;
            double tween;
            if(zoomed) {
                tween = cubicEaseInOut(t, 0, 100, 50);
                setWorldCoordinates(tween - 100, right, tween - 100, top);
            } else {
                tween = cubicEaseInOut(t, 0, 100, 50);
                setWorldCoordinates(-1 * tween, right, -1 * tween, top);
            }

            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluOrtho2D(wcCoords.get("left"), right, wcCoords.get("bottom"), top);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        }
        if(t >= 50) {
            resetT();
        }

        for(ReflectedObject r : reflectedObjects) {
            r.draw(gl);
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {

    }

    /**
     * This method will take a Java Color object and transform it into an array of 3 floats that
     * can be used in glColor3f.
     *
     * @param c The Java Color object to transform into a float array suitable for glColor3f.
     * @return an array of 3 floats. The first being R, second is G, third is B all out of 1 for use in glColor3f.
     */
    public float[] getGLColorArray(Color c) {
        float[] arr = new float[3];

        arr[0] = (float) (c.getRed() / 255.0);
        arr[1] = (float) (c.getGreen() / 255.0);
        arr[2] = (float) (c.getBlue() / 255.0);

        return arr;
    }

    /**
     * Grabs a random color from the palette List.
     *
     * @return An array of 3 floats for R, G, and B, respectively.
     */
    public float[] getRandomPaletteColorArray() {
        Random r = new Random();
        int rand = r.nextInt(palette.size());

        return getGLColorArray(palette.get(rand));
    }

    public void toggleZoom() {
        zoomed = !zoomed;
        currentlyZooming = true;
    }

    /**
     * Cubic ease in/out function taken from http://gizma.com/easing/#cub3
     * I don't really care to take the time to understand the math at the moment,
     * but cubic easing looks way better than linear tweening :)
     *
     * @param t the current frame
     * @param min the number to tween from
     * @param max the number to tween to
     * @param duration the number of frames the tween should last
     * @return the value to tween the value at on the current frame
     */
    public double cubicEaseInOut(double t, int min, int max, int duration) {
        t /= duration/2;
        if(t < 1) {
            return max/2 * Math.pow(t,3) + min;
        }
        t -= 2;
        return max/2 * (Math.pow(t,3) + 2) + min;
    }

    /**
     * resets t for the zoom animation, and also makes sure the zoom animation stops
     */
    public void resetT() {
        t = 0;
        currentlyZooming = false;
    }

    /**
     * @return the JFrame's width
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * @return the JFrame's height
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * The function that will translate the JFrame click coordinates to GL world coordinates
     *
     * @param x the initial click x coordinate
     * @param y the initial click y coordinate
     * @return a point containing the translated x and y coordinates in GL world coordinates
     */
    public Point translateMouseClick(double x, double y) {
        int windowWidth = getWindowWidth();
        int windowHeight = getWindowHeight();

        HashMap<String, Double> wcCoords = getWorldCoordinates();
        double wcWidth = wcCoords.get("right") - wcCoords.get("left");
        double wcHeight = wcCoords.get("top") - wcCoords.get("bottom");

        double newX = x/(windowWidth/wcWidth);
        double newY = (wcHeight - (wcHeight/windowHeight) * y) + wcCoords.get("bottom");

        return new Point(newX, newY);
    }

    /**
     * @return the HashMap containing the values used in gluOrtho2D
     */
    public HashMap<String, Double> getWorldCoordinates() {
        return wcCoords;
    }

    /**
     * sets the HashMap for the gluOrtho2D values
     *
     * @param left the gluOrtho2D left value
     * @param right the gluOrtho2D right value
     * @param bottom the gluOrtho2D bottom value
     * @param top the gluOrtho2D top value
     */
    public void setWorldCoordinates(double left, double right, double bottom, double top) {
        wcCoords.put("left", left);
        wcCoords.put("right", right);
        wcCoords.put("bottom", bottom);
        wcCoords.put("top", top);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_K:
                if(!currentlyZooming) {
                    toggleZoom();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(zoomed) {
            int initialX = e.getX();
            int initialY = e.getY();

            Point p = translateMouseClick(initialX, initialY);

            ReflectedObject r = ReflectedObject.generateShapeAtCoords(p.x, p.y, getRandomPaletteColorArray());
            reflectedObjects.add(r);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
