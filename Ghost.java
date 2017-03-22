import java.util.Random;

import org.newdawn.slick.opengl.Texture;


public class Ghost 
{
	private int type;
	private float patrolRange;
	private float chaseRange;
	private float speed;
	private Position spawnPoint;
	private Position currentPos;
	private Model model;
	private Texture texture;	
	
	public Ghost(int type) 
	{
		switch(type)
		{
			case 0:
				speed = .2f;
				chaseRange = 10;
			break;
			case 1:
				patrolRange = 10;
				chaseRange = 5;
			break;
			case 2:
				speed = .1f;
				patrolRange = 2;
				chaseRange = 2;
			break;
			case 3:
				speed = .025f;
				patrolRange = 10;
				chaseRange = 10;
			break;
		}
		this.type = type;		
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setPos(Position pos)
	{
		if(this.spawnPoint == null)
		{
			this.spawnPoint = new Position(pos.X(), pos.Z(), pos.R());
		}
		this.currentPos = pos;	
	}
	
	public void setModel(Model model)
	{
		this.model = model;
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
	
	public Position patrol()
	{
		double rads = Math.toRadians(currentPos.R());
		switch(type)
		{
			case 0:				
				break;
			case 1:
				this.currentPos.set(spawnPoint.X() - patrolRange*(float)Math.cos(rads), spawnPoint.Z() + patrolRange*(float)Math.sin(rads), currentPos.R() + .1f);
			break;
			case 2:		
				if(this.currentPos.distance2D(spawnPoint) > patrolRange)
				{
					this.currentPos.change(0, 0, 180);
					this.currentPos.change(-speed*(float)Math.sin(rads), -speed*(float)Math.cos(rads), 0);
				}
				else
				{
					this.currentPos.change(speed*(float)Math.sin(rads), speed*(float)Math.cos(rads), 0);
				}
			break;
			case 3:				
				if(this.currentPos.distance2D(spawnPoint) > patrolRange)
				{
					Random rand = new Random();
					this.currentPos.change(0, 0, 270 - rand.nextInt(180));
					this.currentPos.change(-speed*(float)Math.sin(rads), -speed*(float)Math.cos(rads), 0);
				}
				else
				{
					this.currentPos.change(speed*(float)Math.sin(rads), speed*(float)Math.cos(rads), 0);
				}
			break;
				
		}
		return this.currentPos;
	}
	
	public Position chase()
	{
		switch(type)
		{
			case 0:
				
			break;
			
		}
		return this.currentPos;
	}
}
