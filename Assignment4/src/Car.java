import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import java.nio.Buffer;

/**
 * Created by alexanderflasch on 3/26/16.
 */
public class Car {

    GL2 gl;
    GLUT glut;

    double x;

    public Car(GL2 gl) {
        this.gl = gl;
        this.glut = new GLUT();
        x = -20;
    }

    public void draw(GL2 gl, boolean red, boolean cyan) {
        GLUT glut = new GLUT();
        if(cyan) {
            gl.glColor3f(0.0f, 1.0f, 1.0f);
        }
        else if(red) {
            gl.glColor3f(1.0f, 0.0f, 0.0f);
        }
        else {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        }

        // draw body
        gl.glPushMatrix();
            gl.glTranslated(x, -5, 1);

            gl.glPushMatrix();
                gl.glScaled(2.25, 0.5, 0.6);
                gl.glTranslated(0, 0, 0.5);

                glut.glutWireCube(3.0f);
            gl.glPopMatrix();

            // draw cab
            gl.glPushMatrix();
                gl.glScaled(1, 0.5, 0.25);
                gl.glTranslated(-0.5, 3, 0);

                glut.glutWireCube(3.0f);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glTranslated(-3, 0, 2);

                // rear right tire
                gl.glPushMatrix();
                    gl.glRotated(x * -20, 0, 0, 1);

                    drawTire(0, 0, 0);
                gl.glPopMatrix();

                // draw rear axle
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(0, 0, 0);
                    gl.glVertex3d(0, 0, -3);
                gl.glEnd();

                // rear left tire
                gl.glPushMatrix();
                    gl.glTranslated(0, 0, -3);
                    gl.glRotated(x * -20, 0, 0, 1);
                    gl.glTranslated(0, 0, 3);

                    drawTire(0, 0, -3);
                gl.glPopMatrix();

                // front left tire
                gl.glPushMatrix();
                    gl.glTranslated(4, 0, -3);
                    gl.glRotated(x * -20, 0, 0, 1);
                    gl.glTranslated(-4, 0, 3);

                    drawTire(4, 0, -3);
                gl.glPopMatrix();

                // draw front axle
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(4, 0, 0);
                    gl.glVertex3d(4, 0, -3);
                gl.glEnd();

                // front right tire
                gl.glPushMatrix();
                    gl.glTranslated(4, 0, 0);
                    gl.glRotated(x * -20, 0, 0, 1);
                    gl.glTranslated(-4, 0, 0);

                    drawTire(4, 0, 0);
                gl.glPopMatrix();
            gl.glPopMatrix();
        gl.glPopMatrix();
        gl.glFlush();

        x += 0.1;
        if(x >= 20) {
            x = -20;
        }
    }

    public void drawTire(double x, double y, double z) {
        gl.glTranslated(x, y, z);

        for(int i = 0; i < 5; i++) {
//            gl.glPushMatrix();
                gl.glBegin(GL.GL_LINES);
                    gl.glVertex3d(0, -0.5, 0);
                    gl.glVertex3d(0, 0.5, 0);
                gl.glEnd();
                gl.glRotated(45 * i, 0, 0, 1);
//            gl.glPopMatrix();
        }

        glut.glutWireTorus(0.15, 0.5, 10, 25);
    }
}

