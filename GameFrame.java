
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class GameFrame
{	
	private static Map<String, Model> models;
	private static Map<String, Texture> textures;
	private static Digga myDigga;
	private static DiggaCam dCam;
	private static GameWorld myWorld;	
	private static GameClient client;
	private static StaticShader shader;
	
	public static void gameLoop() throws IOException
	{		
		
		myWorld = new GameWorld(client.getID());		
		
		for(int i = 0; i < GameWorld.NUM_DIGGAS; i++)
		{
			myWorld.getDiggas()[i] = new Digga(i, myWorld);	
		}	
		for(int i = 0; i < GameWorld.NUM_GRAVES; i++)
		{
			myWorld.getGraves()[i].setModel(models.get("GraveStone" + myWorld.getGraves()[i].getType() + ".obj"));
			myWorld.getGraves()[i].setTexture(textures.get("GraveStone" + myWorld.getGraves()[i].getType() + ".jpg"));
		}		
		for(int i = 0; i < GameWorld.NUM_GHOSTS; i++)
		{
			myWorld.getGhosts()[i].setModel(models.get("ghost" + myWorld.getGhosts()[i].getType() + ".obj"));
			myWorld.getGhosts()[i].setTexture(textures.get("ghost" + myWorld.getGhosts()[i].getType() + ".jpg"));
		}
		myDigga = myWorld.getDiggas()[client.getID()];
		dCam = new DiggaCam(60, (float)Display.getWidth()/(float)Display.getHeight(), 0.3f, 1000, myDigga);
		
		int fps = 0;
		long timeStamp = System.currentTimeMillis();
		while(!Display.isCloseRequested())
		{
			fps++;
			if(System.currentTimeMillis() - timeStamp > 0)
			{
				fps = 0;
				timeStamp += 1000;
			}
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);					
			glLoadIdentity();		
			
			shader.start(0,0);
			
			dCam.setView();				
			
			drawField();
			for(int i = 0; i < GameWorld.NUM_DIGGAS; i++)
			{
				drawModel(myWorld.getDynamicPoses()[i], myWorld.getDiggas()[i].getModel(), myWorld.getDiggas()[i].getTexture(), .02f);				
			}
			
			for(int i = 0; i < GameWorld.NUM_GRAVES; i++)
			{
				drawModel(myWorld.getStaticPoses()[i], myWorld.getGraves()[i].getModel(), myWorld.getGraves()[i].getTexture(), myWorld.getGraves()[i].getModel().getScale());			
			}
			textures.get("dirt.jpg").bind();
			for(int i = 0; i < GameWorld.NUM_GRAVES; i++)
			{
				myWorld.getGraves()[i].drawPlot(myWorld.getStaticPoses()[i]);				
			}
			myDigga.diggaLoop();	
			for(int i = 0; i < GameWorld.NUM_GHOSTS; i++)
			{
				drawModel(myWorld.getDynamicPoses()[i + GameWorld.NUM_DIGGAS], myWorld.getGhosts()[i].getModel(), myWorld.getGhosts()[i].getTexture(), myWorld.getGhosts()[i].getModel().getScale());	
			}
			shader.stop();			
			Display.update();
		}
		
	}
	
	public static void loadModels() throws IOException
	{
		models = new HashMap<String, Model>(0);
		for(File file : (new File("Models/")).listFiles())
		{
			if(!file.getName().startsWith("yorick"))
			{
				models.put(file.getName(), new Model(file.getName()));	
			}
		}	
	}	
	
	public static void loadTextures() throws IOException
	{		
		textures = new HashMap<String, Texture>(0);
		for(File file : (new File("Textures/")).listFiles())
		{		
			textures.put(file.getName(), TextureLoader.getTexture("jpg", new FileInputStream("Textures/" + file.getName())));		
			GL30.glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D,  GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}		
	}
	
	public static Texture getTexture(String textureName)
	{
		return textures.get(textureName);
	}
	
	public static GameWorld getWorld()
	{
		return myWorld;
	}
	
	public static void drawField()
	{
		float l = GameWorld.WORLD_LENGTH;
		float w = GameWorld.WORLD_WIDTH;
		Model m = models.get("fence.obj");
		Texture t = textures.get("fence.jpg");
		float f = m.getDimensions()[1];
		float s = m.getScale();
		
		for(int i = 0; i < 8; i++)
		{
			drawModel(new Position(-l, -l + f/2f + f*i, 0), m, t, s);
			drawModel(new Position(l, -l + f/2f + f*i, 0), m, t, s);
			drawModel(new Position(-w + f/2f + f*i, w, 90), m, t, s);
			drawModel(new Position(-w + f/2f + f*i, -w, 90), m, t, s);			
			
			
		}	
		textures.get("ground.jpg").bind();
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex3f(-l, 0, -w);
			glTexCoord2f(0, 8);
			glVertex3f(-l, 0, w);
			glTexCoord2f(8, 8);
			glVertex3f(l, 0, w);
			glTexCoord2f(8, 0);
			glVertex3f(l, 0, -w);
		}
		glEnd();
	}
	
	public static void drawModel(Position pos, Model obj, Texture tex, float scale)
	{
		glMatrixMode(GL_MODELVIEW);		
		
		glPushMatrix();
		{		
			glTranslatef(pos.X(), 0, pos.Z());
			glRotatef(pos.R(), 0, 1, 0);
			glScalef(scale, scale, scale);
			
			shader.setModelToWorldMatrix(pos, scale);
			GL30.glBindVertexArray(obj.vaoID);
			GL20.glEnableVertexAttribArray(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureID());
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, obj.faces.size()*3);
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);			
		}
		glPopMatrix();
	}
	
	//temporary draw code for drawing objects
	/*public static void drawRectPrism(Position pos, float centery, float length, float height, float width)
	{
		float centerx = pos.X();
		float centerz = pos.Z();
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		{
			glTranslatef(centerx, centery, centerz);
			glRotatef(pos.R(), 0, 1, 0);
			glTranslatef(-centerx, -centery, -centerz);
			glTranslatef(-length/2f, -height/2f, -width/2f);
			
			
			glBegin(GL_QUADS);
			{
				glColor3f(1.0f, 0, 0);
				glVertex3f(centerx, centery, centerz);
				glVertex3f(centerx + length, centery, centerz);
				glVertex3f(centerx + length, centery + height, centerz);
				glVertex3f(centerx, centery + height, centerz);
				
				//glColor3f(0, 1.0f, 0);
				glVertex3f(centerx, centery, centerz + width);
				glVertex3f(centerx + length, centery, centerz + width);
				glVertex3f(centerx + length, centery + height, centerz + width);
				glVertex3f(centerx, centery + height, centerz + width);
				
				//glColor3f(0, 0, 1.0f);
				glVertex3f(centerx, centery, centerz);
				glVertex3f(centerx, centery + height, centerz);
				glVertex3f(centerx, centery + height, centerz + width);
				glVertex3f(centerx, centery, centerz + width);			
				
				//glColor3f(0, 1.0f, 1.0f);
				glVertex3f(centerx + length, centery, centerz);
				glVertex3f(centerx + length, centery + height, centerz);
				glVertex3f(centerx + length, centery + height, centerz + width);
				glVertex3f(centerx + length, centery, centerz + width);	
				
				//glColor3f(1.0f, 1.0f, 0);
				glVertex3f(centerx, centery, centerz);
				glVertex3f(centerx + length, centery, centerz);
				glVertex3f(centerx + length, centery, centerz + width);
				glVertex3f(centerx, centery, centerz + width);
				
				//glColor3f(1.0f, 0, 1.0f);
				glVertex3f(centerx, centery + height, centerz);
				glVertex3f(centerx + length, centery + height, centerz);
				glVertex3f(centerx + length, centery + height, centerz + width);
				glVertex3f(centerx, centery + height, centerz + width);			
			}
			glEnd();
		}
		glPopMatrix();
	}*/
	
	private static void initGL()
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1000);
		glMatrixMode(GL_MODELVIEW);
		
		glClearColor(0, 0, 0, 1);
	}
	
	private static void initDisplay() throws LWJGLException
	{
		Display.setDisplayMode(new DisplayMode(640, 480));
		Display.create();
	}
	
	public static void main(String [] args) throws IOException, LWJGLException
	{	
		client = new GameClient();
		client.start();
		
		initDisplay();
		initGL();
		
		loadModels();
		loadTextures();
		
		shader = new StaticShader();
		
		
		//make sure client has an id before world is created
		while(client.getID() == -1){}			
		
		gameLoop();
	}
}
