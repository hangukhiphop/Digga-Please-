
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class DiggaCam 
{	
	private float fov;
	private float aspect;
	private float near;
	private float far;
	private FloatBuffer lightBuffer;
	
	private float y;
	
	Digga myDigga;
	
	public DiggaCam(float fo, float a, float n, float f, Digga digga)
	{
		
		fov = fo;
		aspect = a;
		near = n;
		far = f;
		y = 4;
		myDigga = digga;
		initProjection();
	}
	
	private void initProjection()
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, aspect, near, far);
		glMatrixMode(GL_MODELVIEW);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LIGHTING);
		//glEnable(GL_LIGHT0);
		//lightBuffer = BufferUtils.createFloatBuffer(4);
		
		
		//glEnable(GL_CULL_FACE);
		//glCullFace(GL_BACK);
		
	}
	
	public void setView()
	{		
		//float[] lightPos = {myDigga.getPos().X(), 1, myDigga.getPos().Z(), 1f};
		//lightBuffer.put(lightPos);
		//lightBuffer.flip();
		//glLight(GL_LIGHT0, GL_POSITION, lightBuffer);
		
		//third person view
		glRotatef(-myDigga.getPos().R() + 180, 0, 1, 0);
		glTranslatef(-myDigga.getPos().X() + 10*myDigga.FX(), -y, -myDigga.getPos().Z() + 10*myDigga.FZ());		
		
		
		//float[] lightPos = {myDigga.getPos().X(), 5, myDigga.getPos().Z(), 1f};
		
		//top-down view
		//glRotatef(90, 1, 0, 0);
		//glTranslatef(-myDigga.getPos().X(), -3*y, -myDigga.getPos().Z());
	}		
	
	
	
	
	
}
