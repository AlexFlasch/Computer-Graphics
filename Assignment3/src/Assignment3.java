import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;

public class Assignment3 implements GLEventListener, MouseListener, KeyListener {

    public static FPSAnimator fps;

    public static double windowWidth = 800;
    public static double windowHeight = 800;

    public static double wcWidth = 200;
    public static double wcHeight = 200;

    public TextRenderer renderer;
    public String modeText = "Currently in: draw mode";

    public Polygon seed;
    public LinkedList<Polygon> children;
    public Polygon current;
    public LinkedList<Polygon> polygons;

    public double budgeAmount = 1;
    public double rotateAmount = 5;
    public double scaleAmount = 0.02;

    public boolean translateModeOn = false;
    public boolean rotateModeOn = false;
    public boolean scaleModeOn = false;

    public boolean drawingAsFractal = false;
    public int fractalLevels = 0;

    public float[][] levelColors = {
            { 0.8509f, 0.1176f, 0.0941f }, // red
            { 0.9098f, 0.4941f, 0.0156f }, // orange
            { 0.9686f, 0.7921f, 0.0941f }, // yellow
            { 0.1490f, 0.6509f, 0.3568f }, // green
            { 0.2039f, 0.5960f, 0.8588f }, // blue
            { 0.6078f, 0.3490f, 0.7137f }, // purple
            { 0.8509f, 0.1176f, 0.0941f }, // red
            { 0.9098f, 0.4941f, 0.0156f }, // orange
            { 0.9686f, 0.7921f, 0.0941f }  // yellow
    };

    public Assignment3() {
        seed = new Polygon();
        current = seed;
        current.setCurrent(true);

        polygons = new LinkedList<>();
        polygons.add(seed);

        children = new LinkedList<>();
    }

    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();
        Assignment3 assignment3 = new Assignment3();

        GLCapabilities capabilities = new GLCapabilities(GLProfile.getGL2GL3());
        capabilities.setDoubleBuffered(true);
        capabilities.setHardwareAccelerated(true);

        canvas.addGLEventListener(assignment3);

        fps = new FPSAnimator(canvas, 60);

        JFrame cFrame = new JFrame("Assignment3"); // panel containing the canvas
        JFrame hFrame = new JFrame("Controls"); // panel containing the controls
        cFrame.setSize((int) windowWidth, (int) windowHeight);
        cFrame.setResizable(false);
        hFrame.setSize(300, 800);
        hFrame.setLocation(900, 0);
        cFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        hFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // add an informative GUI :)
        JTextArea text = new JTextArea();
        String helpText = "R: toggle rotate mode\n" +
                "T: toggle translate mode\n" +
                "S: toggle scale mode\n" +
                "C: create a new copy of the seed polygon\n" +
                "< >: cycle through the created polygons\n" +
                "\n" +
                "In any mode aside from draw:\n" +
                "\tHold shift to scale, translate, rotate faster" +
                "\n" +
                "In rotate mode: \n" +
                "\tLeft: rotate left\n" +
                "\tRight: rotate right\n" +
                "\n" +
                "In translate mode: \n" +
                "\tLeft: move left\n" +
                "\tRight: move right\n" +
                "\tUp: move up\n" +
                "\tDown: move down\n" +
                "\n" +
                "In scale mode: \n" +
                "\tLeft: scale down X\n" +
                "\tRight: scale up X\n" +
                "\tUp: scale up Y\n" +
                "\tDown: scale down Y\n" +
                "\nWhile creating the seed polygon:\n" +
                "Left Click: create polygon vertices\n" +
                "X: Clear the points, start over drawing\n" +
                "Enter: finalize seed polygon\n";

        text.append(helpText);
        text.setEditable(false);

        text.setVisible(true);
        text.setVisible(true);

        cFrame.add(canvas);
        hFrame.add(text);

        cFrame.setVisible(true);
        hFrame.setVisible(true);
        canvas.requestFocusInWindow();

        canvas.addMouseListener(assignment3);
        canvas.addKeyListener(assignment3);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        renderer = new TextRenderer(new Font("Sans Serif", Font.PLAIN, 24));

        // make things pretty and antialias everything :)
        if (gl.isExtensionAvailable("GL_ARB_multisample")) {
            gl.glEnable(GL.GL_MULTISAMPLE);
        }

        gl.glClearColor(0.4235f, 0.4784f, 0.5372f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluOrtho2D(-100, 100, -100, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        fps.start();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glColor3f(0.0f, 0.0f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderer.draw(modeText, 0, 0);
        renderer.endRendering();

        if(!drawingAsFractal) {
            for(Polygon p : polygons) {
                p.draw(gl, null);
            }
        }
        else {
            drawByLevel(gl, fractalLevels);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                scaleAmount *= 5;
                rotateAmount *= 5;
                budgeAmount *= 5;
                break;

            case KeyEvent.VK_ENTER:
                seed.finalizePolygon();
                break;

            case KeyEvent.VK_X:
                if(!seed.finalized) {
                    seed.clearPoints();
                }
                break;

            case KeyEvent.VK_C:
                copySeedPolygon();
                break;

            case KeyEvent.VK_T:
                toggleTranslateMode();
                break;

            case KeyEvent.VK_R:
                toggleRotateMode();
                break;

            case KeyEvent.VK_S:
                toggleScaleMode();
                break;

            case KeyEvent.VK_PERIOD:
                cycleNextPolygon();
                break;

            case KeyEvent.VK_COMMA:
                cyclePrevPolygon();
                break;

            case KeyEvent.VK_UP:
                if(scaleModeOn) {
                    current.scale(0, scaleAmount);
                }
                else if(translateModeOn) {
                    current.budge(0.0, budgeAmount);
                }
                break;

            case KeyEvent.VK_DOWN:
                if(scaleModeOn) {
                    current.scale(0, -scaleAmount);
                }
                else if(translateModeOn) {
                    current.budge(0.0, -budgeAmount);
                }
                break;

            case KeyEvent.VK_LEFT:
                if(rotateModeOn) {
                    current.rotate(rotateAmount);
                }
                else if(scaleModeOn) {
                    current.scale(-scaleAmount, 0);
                }
                else if(translateModeOn) {
                    current.budge(-budgeAmount, 0);
                }
                break;

            case KeyEvent.VK_RIGHT:
                if(rotateModeOn) {
                    current.rotate(-rotateAmount);
                }
                else if(scaleModeOn) {
                    current.scale(scaleAmount, 0);
                }
                else if(translateModeOn) {
                    current.budge(budgeAmount, 0);
                }
                break;

            case KeyEvent.VK_0:
                drawAsFractal(0);
                break;

            case KeyEvent.VK_1:
                drawAsFractal(1);
                break;

            case KeyEvent.VK_2:
                drawAsFractal(2);
                break;

            case KeyEvent.VK_3:
                drawAsFractal(3);
                break;

            case KeyEvent.VK_4:
                drawAsFractal(4);
                break;

            case KeyEvent.VK_5:
                drawAsFractal(5);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                scaleAmount /= 5;
                rotateAmount /= 5;
                budgeAmount /= 5;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        HashMap<String, Double> map = translateMouseClick(x, y);
        x = map.get("x");
        y = map.get("y");

        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                if(!seed.finalized) {
                    seed.createPoint(x, y);
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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

    public void drawAsFractal(int levels) {
        fractalLevels = levels;
        drawingAsFractal = true;
    }

    public void drawByLevel(GL2 gl, int levels) {
        Polygon currentSeed = seed;

        float[] levelColor = levelColors[fractalLevels - levels];
        gl.glPushMatrix();
        seed.draw(gl, levelColor);
        for(Polygon p : children) {
            drawByLevel(gl, levels - 1, p);
        }
        gl.glPopMatrix();
    }

    public void drawByLevel(GL2 gl, int levels, Polygon currentSeed) {
        if(levels <= 0) {
            return;
        }

        float[] levelColor = levelColors[fractalLevels - levels];
        Polygon temp = Polygon.copyPolygon(currentSeed);
        LinkedList<Polygon> tempChildren = temp.getChildren(children);

        gl.glPushMatrix();
        temp.draw(gl, levelColor);
        for(Polygon p : tempChildren) {
            drawByLevel(gl, levels - 1, p);
        }
        gl.glPopMatrix();
    }

    public void writeIfsFile() {

    }

    public HashMap<String, Double> translateMouseClick(double x, double y) {
        double newX = x / (windowWidth / wcWidth) - (wcWidth / 2);
        double newY = (wcHeight - (wcHeight / windowHeight) * y) + (-1 * (wcHeight / 2));

        HashMap<String, Double> map = new HashMap<>();
        map.put("x", newX);
        map.put("y", newY);

        return map;
    }

    public void cycleNextPolygon() {
        int currentIndex = polygons.indexOf(current);
        current.setCurrent(false);
        currentIndex++;
        if(currentIndex > polygons.size() - 1) {
            currentIndex = 0;
        }
        current = polygons.get(currentIndex);
        current.setCurrent(true);
    }

    public void cyclePrevPolygon() {
        int currentIndex = polygons.indexOf(current);
        current.setCurrent(false);
        currentIndex--;
        if(currentIndex < 0) {
            currentIndex = polygons.size() - 1;
        }
        current = polygons.get(currentIndex);
        current.setCurrent(true);
    }

    public void copySeedPolygon() {
        Polygon temp = Polygon.copyPolygon(seed);
        polygons.add(temp);
        children.add(temp);
        current.setCurrent(false);
        current = temp;
        current.setCurrent(true);
    }

    public void toggleTranslateMode() {
        translateModeOn = !translateModeOn;
        rotateModeOn = false;
        scaleModeOn = false;

        if(translateModeOn) {
            modeText = "Currently in: translate mode";
        }
        else {
            modeText = "Currently in: draw mode";
        }
    }

    public void toggleRotateMode() {
        rotateModeOn = !rotateModeOn;
        translateModeOn = false;
        scaleModeOn = false;

        if(rotateModeOn) {
            modeText = "Currently in: rotate mode";
        }
        else {
            modeText = "Currently in: draw mode";
        }
    }

    public void toggleScaleMode() {
        scaleModeOn = !scaleModeOn;
        translateModeOn = false;
        rotateModeOn = false;

        if(scaleModeOn) {
            modeText = "Currently in: scale mode";
        }
        else {
            modeText = "Currently in: draw mode";
        }
    }
}
