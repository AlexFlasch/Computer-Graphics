import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Created by alexanderflasch on 3/26/16.
 */
public class Mobile {

    public double x;
    public int rot;
    public double rotation;

    public GLUT glut;
    public GLU glu;

    public Mobile(GL2 gl) {
        glut = new GLUT();
        glu = new GLU();

        x = 0;
        rot = 1;
    }

    public void draw(GL2 gl, boolean red, boolean cyan) {
        if(cyan) {
            gl.glColor3f(0.0f, 1.0f, 1.0f);
        }
        else if(red) {
            gl.glColor3f(1.0f, 0.0f, 0.0f);
        }
        else {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        }
        gl.glLineWidth(2f);


        gl.glPushMatrix();
            gl.glTranslated(7, 10, 0);
            gl.glRotated(rotation, 0, 1, 0);

            // draw stem
            gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(0, 0, 0);
                gl.glVertex3d(0, -1, 0);
            gl.glEnd();

            // draw left arm
            gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(0, -1, 0);
                gl.glVertex3d(-2, -1, 0);
            gl.glEnd();
            gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(-2, -1, 0);
                gl.glVertex3d(-2, -2, 0);
            gl.glEnd();

            // draw top left dangley
            gl.glPushMatrix();
                gl.glTranslated(-2, -2.5, 0);
                gl.glRotated(rotation, 0, 1, 0);
                gl.glTranslated(2, 2.5, 0);

                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(-2, -2, 0);
                    gl.glVertex3d(-2.5, -2.5, 0);
                    gl.glVertex3d(-2.5, -2.5, 0);
                    gl.glVertex3d(-2, -3, 0);
                    gl.glVertex3d(-2, -3, 0);
                    gl.glVertex3d(-1.5, -2.5, 0);
                    gl.glVertex3d(-1.5, -2.5, 0);
                    gl.glVertex3d(-2, -2, 0);
                gl.glEnd();
            gl.glPopMatrix();

            // draw right arm
            gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(0, -1, 0);
                gl.glVertex3d(2, -1, 0);
            gl.glEnd();
            gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(2, -1, 0);
                gl.glVertex3d(2, -1.5, 0);
            gl.glEnd();

            // draw top right dangley
            gl.glPushMatrix();
                gl.glTranslated(2, -1.75, 0);
                gl.glRotated(rotation, 0, 1, 0);
                gl.glTranslated(-2, 1.75, 0);

                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(2, -1.5, 0);
                    gl.glVertex3d(1.5, -2, 0);
                    gl.glVertex3d(1.5, -2, 0);
                    gl.glVertex3d(2.5, -2, 0);
                    gl.glVertex3d(2.5, -2, 0);
                    gl.glVertex3d(2, -1.5, 0);
                gl.glEnd();
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotated(-rotation * 2, 0, 1, 0);

                // draw string to second set of arms
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(0, -1, 0);
                    gl.glVertex3d(0, -3, 0);
                gl.glEnd();

                // draw bottom left arm
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(0, -3, 0);
                    gl.glVertex3d(-3, -3, 0);
                gl.glEnd();
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(-3, -3, 0);
                    gl.glVertex3d(-3, -4, 0);
                gl.glEnd();

                // draw left dangley
                gl.glPushMatrix();
                    gl.glTranslated(-3, -4.5, 0);
                    gl.glRotated(rotation * 2, 0, 1, 0);
                    gl.glTranslated(3, 4.5, 0);

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex3d(-3, -4, 0);
                        gl.glVertex3d(-3.5, -4, 0);
                        gl.glVertex3d(-3.5, -4, 0);
                        gl.glVertex3d(-3.5, -5, 0);
                        gl.glVertex3d(-3.5, -5, 0);
                        gl.glVertex3d(-2.5, -5, 0);
                        gl.glVertex3d(-2.5, -5, 0);
                        gl.glVertex3d(-2.5, -4, 0);
                        gl.glVertex3d(-2.5, -4, 0);
                        gl.glVertex3d(-3, -4, 0);
                    gl.glEnd();
                gl.glPopMatrix();

                // draw bottom right arm
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(0, -3, 0);
                    gl.glVertex3d(2, -3, 0);
                gl.glEnd();
                gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3d(2, -3, 0);
                    gl.glVertex3d(2, -5, 0);
                gl.glEnd();

                // draw right dangley
                gl.glPushMatrix();
                    gl.glTranslated(2, -5.5, 0);
                    gl.glRotated(rotation * 2, 0, 1, 0);
                    gl.glTranslated(-2, 5.5, 0);

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex3d(2, -5, 0);
                        gl.glVertex3d(1.5, -5, 0);
                        gl.glVertex3d(1.5, -5, 0);
                        gl.glVertex3d(2, -6, 0);
                        gl.glVertex3d(2, -6, 0);
                        gl.glVertex3d(2.5, -5, 0);
                        gl.glVertex3d(2.5, -5, 0);
                        gl.glVertex3d(2, -5, 0);
                    gl.glEnd();
                gl.glPopMatrix();

            gl.glPopMatrix();

        gl.glPopMatrix();

        if(x >= 360) {
            x = 0;
        }
        x += 0.25;

        rotation = x * rot;
    }

    public void flipRotation() {
        rot *= -1;
    }
}
