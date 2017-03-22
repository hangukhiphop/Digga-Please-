import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class GameClient extends Thread
{
	private int ID;
	private BufferedReader in;
	private ObjectOutputStream positionOOS;
	private ObjectInputStream positionOIS;
	
	public GameClient()
	{
		ID = -1;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void run()
	{
		try 
		{
			Socket socket = new Socket("localhost", 9001);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ID = Integer.parseInt(in.readLine());
			System.out.println(ID);
			positionOOS = new ObjectOutputStream(socket.getOutputStream());
			positionOIS = new ObjectInputStream(socket.getInputStream());		
			
			while(GameFrame.getWorld() == null){System.out.print("");}
			
			while(true)
			{				
				positionOOS.reset();
				positionOOS.writeObject(GameFrame.getWorld().getDynamicPoses()[ID]);
				positionOOS.flush();
				Thread.sleep(10);
				Position[] poses = (Position[])positionOIS.readObject();
				for(int i = 0; i < poses.length; i++)
				{
					if(poses[i] == null)
					{
						break;
					}
					String name = poses[i].OBJ();
					if(name.startsWith("digga") && !name.contains("" + ID))
					{
						GameFrame.getWorld().changeDiggaPos(Integer.parseInt(name.substring(name.indexOf("a") + 1, name.length())), poses[i]);
						
						if(GameFrame.getWorld().getDiggas()[i] != null && GameFrame.getWorld().getDiggas()[i].getBBox() != null)
						{
							GameFrame.getWorld().getDiggas()[i].getBBox().setBounds(poses[i]);
						}
					}
					else if(name.contains("ghost"))
					{
						if(GameFrame.getWorld().getGhosts()[i - GameWorld.NUM_DIGGAS] == null)
						{
							GameFrame.getWorld().getGhosts()[i - GameWorld.NUM_DIGGAS] = new Ghost(Integer.parseInt(name.substring(0, 1)));
						}
						GameFrame.getWorld().changeGhostPos(i - GameWorld.NUM_DIGGAS, poses[i]);
					}
					else if(name.contains("grave"))
					{
						GameFrame.getWorld().getGraves()[i] = new Grave(Integer.parseInt(name.substring(0, 1)));
						GameFrame.getWorld().changeGravePos(i, poses[i]);
					}
					
				}
			}
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}		
	}
}
