import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;

/**
 * Created by alexanderflasch on 3/25/16.
 */
public class Assignment4 implements GLEventListener, KeyListener {

    public static FPSAnimator fps;

    public Car car;
    public Mobile mobile;

    public double rotAmount;
    public boolean draw3d;

    public GLU glu;
    public GLUT glut;

    public Assignment4() {
        glu = new GLU();
        glut = new GLUT();

        rotAmount = 2;
        draw3d = true;
    }

    public static void main(String[] args) {
        GLJPanel canvas = new GLJPanel();
        Assignment4 assignment4 = new Assignment4();

        GLCapabilities capabilities = new GLCapabilities(GLProfile.getGL2GL3());
        capabilities.setDoubleBuffered(true);
        capabilities.setHardwareAccelerated(true);

        canvas.addGLEventListener(assignment4);

        fps = new FPSAnimator(canvas, 60);

        JFrame panel = new JFrame();
        panel.setSize(800, 800);
        panel.setResizable(false);
        panel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel.add(canvas);
        panel.setVisible(true);
        panel.requestFocusInWindow();

        panel.addKeyListener(assignment4);
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        car = new Car(gl);
        mobile = new Mobile(gl);

        glu = new GLU();
        glut = new GLUT();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-10.0, 10.0, -10.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        glu.gluPerspective(25f, 1f, 1f, 20f);
        glu.gluLookAt(0f, 1.0f, 7.0f,
                0f, 0f, 0f,
                0f, 1f, 0f);


        fps.start();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        car.draw(gl, false, false);
        mobile.draw(gl, false, false);
        if(draw3d) {
            drawAnaglyphs(gl);
        }
    }

    private void drawAnaglyphs(GL2 gl) {
        // draw cyan
        gl.glPushMatrix();
            gl.glColor3f(0.0f, 1.0f, 1.0f);

            gl.glRotated(-rotAmount, 0, 1, 0);
            car.draw(gl, false, true);
            mobile.draw(gl, false, true);
        gl.glPopMatrix();

        // draw red
        gl.glPushMatrix();
            gl.glColor3f(1.0f, 0.0f, 0.0f);

            gl.glRotated(rotAmount, 0, 1, 0);
            car.draw(gl, true, false);
            mobile.draw(gl, true, false);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_R:
                mobile.flipRotation();
                System.out.println("R pressed!");
                break;
            case KeyEvent.VK_LEFT:
                rotAmount -= 0.1;
                System.out.println("Left pressed! rotAmount: " + rotAmount);
                break;
            case KeyEvent.VK_RIGHT:
                rotAmount += 0.1;
                System.out.println("Right pressed!");
                break;
            case KeyEvent.VK_SPACE:
                draw3d = !draw3d;
                System.out.println("Space pressed!");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
