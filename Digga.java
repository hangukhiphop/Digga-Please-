import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;


public class Digga 
{
	int ID;
	private float xForward;
	private float zForward;
	private GameWorld myWorld;
	private Model myModel;
	private Texture myTexture;
	private BoundingBox bBox;
	public float collisionRadius;
	
	public Digga(int id, GameWorld myWorld) throws FileNotFoundException, IOException
	{
		ID = id;
		switch(ID)
		{
			case 0:
				myModel = new Model("yorick.obj");
				myTexture = GameFrame.getTexture("yorick_base_CM_TX.jpg");
				break;
			case 1:
				myModel = new Model("yorick_undertaker.obj");
				myTexture = GameFrame.getTexture("yorick_undertaker_TX_CM.jpg");
				break;
			case 2:
				myModel = new Model("yorick_pentakill.obj");
				myTexture = GameFrame.getTexture("Yorick_pentakill_TX_CM.jpg");
				break;
			case 3:
				myModel = new Model("yorick.obj");
				myTexture = GameFrame.getTexture("cats.jpg");
				break;
		}
		this.collisionRadius = (float)Math.sqrt(Math.pow(myModel.getDimensions()[0], 2) + Math.pow(myModel.getDimensions()[1], 2));
		bBox = new BoundingBox(myModel.getCenter()[0], myModel.getCenter()[1], myModel.getDimensions()[0], myModel.getDimensions()[1]);
		bBox.setBounds(myWorld.getDynamicPoses()[ID]);
		this.myWorld = myWorld;
		
		setVectors();
	}
	
	public Position getPos()
	{
		return myWorld.getDynamicPoses()[ID];
	}	
	
	public float FX()
	{
		return xForward;
	}
	
	public float FZ()
	{
		return zForward;
	}
	
	public void setVectors()
	{
		xForward = (float)(Math.sin(Math.toRadians(myWorld.getDynamicPoses()[ID].R())));
		zForward = (float)(Math.cos(Math.toRadians(myWorld.getDynamicPoses()[ID].R())));		
	}
	
	public Model getModel()
	{
		return myModel;
	}
	
	public Texture getTexture()
	{
		return myTexture;
	}
	
	public BoundingBox getBBox()
	{
		return bBox;
	}
	
	public void diggaLoop()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			forward(.005f);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			forward(-.005f);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			turn(-.025f);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			turn(.025f);
		}
		
		
	}
	
	public void forward(float speed)
	{
		myWorld.getDynamicPoses()[ID].change(xForward*speed, zForward*speed, 0);
		bBox.setBounds(myWorld.getDynamicPoses()[ID]);
		while(myWorld.checkCollisions(ID) || outOfBounds())
		{
			myWorld.getDynamicPoses()[ID].change(-xForward*speed/10f, -zForward*speed/10f, 0);
			bBox.setBounds(myWorld.getDynamicPoses()[ID]);		
		}	
	}
	
	public void turn(float ang)
	{
		myWorld.getDynamicPoses()[ID].change(0, 0, ang);
		bBox.setBounds(myWorld.getDynamicPoses()[ID]);
		while(myWorld.checkCollisions(ID) || outOfBounds())
		{
			myWorld.getDynamicPoses()[ID].change(0, 0, -ang/10f);
			bBox.setBounds(myWorld.getDynamicPoses()[ID]);		
		}
		setVectors();	
	}
	
	public boolean outOfBounds()
	{
		for(int i = 0; i < 4; i++)
		{
			if(bBox.x[i] < -GameWorld.WORLD_LENGTH || bBox.x[i] > GameWorld.WORLD_LENGTH || bBox.x[i] < -GameWorld.WORLD_WIDTH || bBox.x[i] > GameWorld.WORLD_WIDTH)
			{
				return true;
			}
		}
		return false;
	}
	
	//public int checkGhosts()
	//{
		
	//}
}
