import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;


public class Grave
{
	int type;
	float digState = .001f;
	float length = 5;
	float width = 2;
	private Position pos;
	private Model model;
	private Texture texture;
	private BoundingBox bBox;
	
	public Grave(int type) 
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setPos(Position pos)
	{
		this.pos = pos;
		
	}
	
	public void setModel(Model model)
	{
		this.model = model;
		bBox = new BoundingBox(0, 0, model.getDimensions()[0], model.getDimensions()[1]);
		bBox.setBounds(pos);
	}
	
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
	
	public Model getModel()
	{
		return model;
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public BoundingBox getBBox()
	{
		return bBox;
	}
	
	public void drawPlot(Position pos)
	{
		float x = pos.X();
		float z = pos.Z();
		glPushMatrix();
		{
			glTranslatef(x, 0, z);
			glRotatef(pos.R(), 0, 1, 0);
			glTranslatef(-x, 0, -z);
		glBegin(GL_QUADS);
		{			
			glColor3f(0, 0, 0);
			GL11.glTexCoord2f(0, 0);
			glVertex3f(bBox.x[3], digState, z - width/2f);
			GL11.glTexCoord2f(0, 1);
			glVertex3f(bBox.x[3] + length, digState, z - width/2f);
			GL11.glTexCoord2f(1, 1);
			glVertex3f(bBox.x[3] + length, digState, z + width/2f);
			GL11.glTexCoord2f(1, 0);
			glVertex3f(bBox.x[3], digState, z + width/2f);			
			
			
			glVertex3f(bBox.x[0], .1f, bBox.z[0]);			
			glVertex3f(bBox.x[1], .1f, bBox.z[1]);			
			glVertex3f(bBox.x[2], .1f, bBox.z[2]);
			glVertex3f(bBox.x[3], .1f, bBox.z[3]);
			
			
			/*glColor3f(139f/510f, 69f/510f, 19f/510f);
			glVertex3f(x - length/2f, 0, z + width/2f + 5f);
			glVertex3f(x - length/2f, -1, z + width/2f + 5f);
			glVertex3f(x + length/2f, -1, z + width/2f + 5f);
			glVertex3f(x + length/2f, 0, z + width/2f + 5f);
			
			glVertex3f(x - length/2f, 0, z + width/2f);
			glVertex3f(x - length/2f, -1, z + width/2f);
			glVertex3f(x + length/2f, -1, z + width/2f);
			glVertex3f(x + length/2f, 0, z + width/2f);
			
			glVertex3f(x + length/2f, 0, z + width/2f);
			glVertex3f(x + length/2f, -1, z + width/2f);
			glVertex3f(x + length/2f, -1, z + width/2f + 5f);
			glVertex3f(x + length/2f, 0, z + width/2f + 5f);
			
			glVertex3f(x - length/2f, 0, z + width/2f);
			glVertex3f(x - length/2f, -1, z + width/2f);
			glVertex3f(x - length/2f, -1, z + width/2f + 5f);
			glVertex3f(x - length/2f, 0, z + width/2f + 5f);*/
		}		
		glEnd();	
		}
		glPopMatrix();
	}
}
