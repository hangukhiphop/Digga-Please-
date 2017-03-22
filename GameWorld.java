import java.io.IOException;
import java.util.Random;


public class GameWorld 
{
	final static int WORLD_LENGTH = 64;
	final static int WORLD_WIDTH = 64;
	final static int NUM_DIGGAS = 4;
	final static int NUM_GRAVES = 16;
	final static int NUM_GRAVE_TYPES = 2;
	final static int NUM_GHOSTS = 16;
	final static int NUM_GHOST_TYPES = 4;
	final static int BLOCKS_PER_DIM = 2;
	
	private Position[] spawnPoints;	
	
	private Digga [] diggas;
	private Grave [] graves;
	private Ghost [] ghosts;
	
	//positions that change per frame (diggas, ghosts)
	private Position [] staticPoses;
	//positions that are set once by the server and never updated (graves, trees)
	private Position [] dynamicPoses;
	
	
	public GameWorld(int id) throws IOException
	{
		
		dynamicPoses = new Position[NUM_DIGGAS + NUM_GHOSTS];
			
		ghosts = new Ghost[NUM_GHOSTS];			
		diggas = new Digga[NUM_DIGGAS];
		spawnPoints = new Position[NUM_DIGGAS];
		staticPoses = new Position[NUM_GRAVES];	
		graves = new Grave[NUM_GRAVES];	
		
		for(int i = 0; i < NUM_DIGGAS; i++)
		{
			spawnPoints[i] = new Position("digga" + i, (-WORLD_LENGTH + 5)*(float)Math.pow(-1, i), (-WORLD_WIDTH + 5)*(float)Math.pow(-1, i)*(1 - 2*(i/2)), -90*i);
			dynamicPoses[i] = spawnPoints[i];
		}			
		
		if(id == -1)
		{
			Random randomgen = new Random(System.currentTimeMillis());
			float randx;
			float randz;
			int randType;
			
			int blockLength = WORLD_LENGTH/BLOCKS_PER_DIM;
			int blockWidth = WORLD_WIDTH/BLOCKS_PER_DIM;
			for(int i = 0; i < 2*BLOCKS_PER_DIM; i++)
			{
				for(int j = 0; j < 2*BLOCKS_PER_DIM; j++)
				{
					randx = (i - BLOCKS_PER_DIM)*blockLength + randomgen.nextFloat()*randomgen.nextInt(blockLength);
					randz = (j - BLOCKS_PER_DIM)*blockWidth + randomgen.nextFloat()*randomgen.nextInt(blockWidth);
					randType = randomgen.nextInt(NUM_GRAVE_TYPES);
					graves[2*BLOCKS_PER_DIM*i + j] = new Grave(randType);
					staticPoses[2*BLOCKS_PER_DIM*i + j] = new Position(randType + "grave", randx, randz, randomgen.nextFloat()*randomgen.nextInt(360));	
					randx = (i - BLOCKS_PER_DIM)*blockLength + randomgen.nextFloat()*randomgen.nextInt(blockLength);
					randz = (j - BLOCKS_PER_DIM)*blockWidth + randomgen.nextFloat()*randomgen.nextInt(blockWidth);
					randType = randomgen.nextInt(NUM_GHOST_TYPES);
					ghosts[2*BLOCKS_PER_DIM*i + j] = new Ghost(randType);					
					dynamicPoses[2*BLOCKS_PER_DIM*i + j + NUM_DIGGAS] = new Position(randType + "ghost", randx, randz, randomgen.nextFloat()*randomgen.nextInt(360));
					ghosts[2*BLOCKS_PER_DIM*i + j].setPos(dynamicPoses[2*BLOCKS_PER_DIM*i + j + NUM_DIGGAS]);
				}
			}
			
		}
		
	}
	
	public Digga[] getDiggas()
	{
		return diggas;
	}
	
	public Grave[] getGraves()
	{
		return graves;
	}
	
	public Ghost[] getGhosts()
	{
		return ghosts;
	}
	
	public Position[] getStaticPoses()
	{
		return staticPoses;
	}
	
	public Position[] getDynamicPoses()
	{
		return dynamicPoses;
	}	
	
	public void changeDiggaPos(int i, Position pos) 
	{
		dynamicPoses[i] = pos;	
	}	
	
	public void changeGravePos(int i, Position pos)
	{
		graves[i].setPos(pos);
		staticPoses[i] = pos;
	}
	
	public void changeGhostPos(int i, Position pos)
	{
		ghosts[i].setPos(pos);
		dynamicPoses[i + NUM_DIGGAS] = pos;
	}
	
	public boolean checkCollisions(int diggaID)
	{
		
		for(int i = 0; i < NUM_GRAVES; i++)
		{
			if(graves[i] != null && (Math.pow(staticPoses[i].X() - dynamicPoses[diggaID].X(), 2) + Math.pow(staticPoses[i].Z() - dynamicPoses[diggaID].Z(),  2)) < diggas[diggaID].collisionRadius)
			{
				if(diggas[diggaID].getBBox().collisionCheck(graves[i].getBBox()))
				{
					return true;
				}
			}
		}
		for(int i = 0; i < NUM_DIGGAS; i++)
		{
			if(diggas[i] != null && i != diggaID && (Math.pow(dynamicPoses[diggaID].X() - dynamicPoses[i].X(), 2) + Math.pow(dynamicPoses[diggaID].Z() - dynamicPoses[i].Z(), 2)) < diggas[diggaID].collisionRadius)
			{
				if(i != diggaID && diggas[diggaID].getBBox().collisionCheck(diggas[i].getBBox()))
				{
					return true;
				}
			}
		}
		
		return false;
		
		
	}
}
