import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server
{
	private static int numConnections = 0;
	private static final int PORT = 9001;
	public GameWorld realWorld;
	
	public static void main(String [] args) throws IOException
	{
		Server server = new Server();
	}
	
	public Server() throws IOException
	{
		realWorld = new GameWorld(-1);
		ServerSocket listener = new ServerSocket(PORT);
		
		while (numConnections < 4)
		{
			new GameServer(listener.accept(), this).start();
			numConnections++;
		}
	}
	public static int getNumConnections()
	{
		return numConnections;
	}
	
	public Server Get()
	{
		return this;
	}
}

class GameServer extends Thread
{
	private Socket socket;
	private PrintWriter IDPW;
	private ObjectInputStream positionOIS;
	private ObjectOutputStream positionOOS;
	private Server server;
	
	public GameServer(Socket socket, Server server)
	{
		this.socket = socket;
		this.server = server;
	}
	
	public void moveGhosts()
	{
		for(int i = 0; i < GameWorld.NUM_GHOSTS; i++)
		{
			server.realWorld.changeGhostPos(i, server.realWorld.getGhosts()[i].patrol());
		}		
	}
	public void run()
	{
		try
		{			
			IDPW = new PrintWriter(socket.getOutputStream());
			IDPW.println(Server.getNumConnections() - 1);
			IDPW.flush();	
			
			positionOIS = new ObjectInputStream(socket.getInputStream());
			positionOOS = new ObjectOutputStream(socket.getOutputStream());					
			positionOOS.writeObject(server.realWorld.getStaticPoses());
			positionOOS.flush();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		while(true)
		{
			try 
			{		
			
				Position dPos = (Position)positionOIS.readObject();
				String name = dPos.OBJ();
				if(name.startsWith("digga"))
				{
					server.realWorld.changeDiggaPos(Integer.parseInt(name.substring(name.indexOf("a") + 1, name.length())), dPos);
				}	
				moveGhosts();
				positionOOS.reset();
				positionOOS.writeObject(server.realWorld.getDynamicPoses()); 	
				positionOOS.flush();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			} 
			
		}
	}
	
}
