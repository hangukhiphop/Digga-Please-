import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class Model 
{
	public int vaoID;
	public ArrayList<Vertex> vertices;
	public ArrayList<Face> faces;
	private ArrayList<TexCoord> texCoords;
	private FileReader fReader;
	private BufferedReader bReader;
	private float scale;
	private float length;
	private float height;
	private float width;
	private float centerx;
	private float centerz;
	
	
	public Model(String OBJFile) throws IOException
	{
		fReader = new FileReader("Models/" + OBJFile);
		bReader = new BufferedReader(fReader);
		
		vertices = new ArrayList<Vertex>();
		faces = new ArrayList<Face>();
		texCoords = new ArrayList<TexCoord>();
		
		float xMin = Integer.MAX_VALUE;
		float yMin = Integer.MAX_VALUE;
		float zMin = Integer.MAX_VALUE;
		float xMax = Integer.MIN_VALUE;
		float yMax = Integer.MIN_VALUE;
		float zMax = Integer.MIN_VALUE;
		
		scale = 1;
		
		String line;
		String [] contents = new String[4];
		do
		{
			line = bReader.readLine();
			if(line == null)
			{
				break;
			}
			
			contents = line.split(" ");
			
			if(line.startsWith("v "))
			{		
				float[] coordinates = {Float.parseFloat(contents[1]), Float.parseFloat(contents[2]), Float.parseFloat(contents[3])};
				vertices.add(new Vertex(coordinates[0], coordinates[1], coordinates[2]));	
				xMax = Math.max(xMax, coordinates[0]);				
				xMin = Math.min(xMin, coordinates[0]);
				yMax = Math.max(yMax, coordinates[1]);	
				yMin = Math.min(yMin, coordinates[1]);
				zMax = Math.max(zMax, coordinates[2]);	
				zMin = Math.min(zMin, coordinates[2]);
			}
			else if(line.startsWith("vt "))
			{
				texCoords.add(new TexCoord(Float.parseFloat(contents[1]), Float.parseFloat(contents[2])));
			}
			else if(line.startsWith("f "))
			{	
				Vertex[] faceVerts = new Vertex[3];
				TexCoord[] faceTexes = new TexCoord[3];
				for(int i = 0; i < 3; i++)
				{
					String[] contentsOfContents = contents[i + 1].split("/");
					faceVerts[i] = vertices.get(Integer.parseInt(contentsOfContents[0]) - 1);
					faceTexes[i] = texCoords.get(Integer.parseInt(contentsOfContents[1]) - 1);
				}
				faces.add(new Face(faceVerts, faceTexes));
			}			
		}while(line != null);		
		
		float[] vCoordArr = new float[faces.size()*9];
		float[] tCoordArr = new float[faces.size()*6];
		for(int i = 0; i < faces.size(); i++)
		{
			for(int j = 0; j < 3; j++)
			{
				vCoordArr[9*i + 3*j] = faces.get(i).vertices[j].x;
				vCoordArr[9*i + 3*j + 1] = faces.get(i).vertices[j].y;
				vCoordArr[9*i + 3*j + 2] = faces.get(i).vertices[j].z;
				
				tCoordArr[6*i + 2*j] = faces.get(i).texCoords[j].u;
				tCoordArr[6*i + 2*j + 1] = 1 - faces.get(i).texCoords[j].v;
			}
		}
		FloatBuffer vertexFB = BufferUtils.createFloatBuffer(vCoordArr.length);
		vertexFB.put(vCoordArr);
		vertexFB.flip();
		FloatBuffer textureFB = BufferUtils.createFloatBuffer(tCoordArr.length);
		textureFB.put(tCoordArr);
		textureFB.flip();
		
		vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        System.out.println(vaoID + " " + OBJFile);
        
        int vboID = GL15.glGenBuffers();        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,  vertexFB,  GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,  textureFB,  GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);         
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        GL30.glBindVertexArray(0);
        
		length = xMax - xMin;
		height = yMax - yMin;
		width = zMax - zMin;		
		
		
		if(OBJFile.startsWith("yorick"))
		{
			scale = .02f;
		}
		else if(OBJFile.startsWith("GraveStone"))
		{
			float lRatio = 1f/length;
			float hRatio = 5f/height;
			float wRatio = 2f/width;
			scale = Math.min(wRatio, Math.min(lRatio, hRatio));
		}
		else if(OBJFile.equals("fence.obj"))
		{
			scale = 16f/width;
		}
		else if(OBJFile.startsWith("ghost"))
		{
			float lRatio = 6f/length;
			float hRatio = 4f/height;
			float wRatio = 6f/width;
			scale = Math.min(wRatio, Math.min(lRatio, hRatio));
		}
		length *= scale;
		height *= scale;
		width *= scale;
		
		centerx = (xMax + xMin)*.5f*scale;
		centerz = (zMax + zMin)*.5f*scale;
		
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public float[] getDimensions()
	{
		float[] dimensions = {length, width};
		return dimensions;
	}
	
	public float[] getCenter()
	{
		float[] center = {centerx, centerz};
		return center;
		
	}
}

class Vertex
{
	float x;
	float y;
	float z;
	public Vertex(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class TexCoord
{
	float u;
	float v;
	public TexCoord(float u, float v)
	{
		this.u = u;
		this.v = v;
	}
}

class Face
{
	Vertex[] vertices;
	TexCoord[] texCoords;
	public Face(Vertex [] verts, TexCoord[] texes)
	{
		vertices = verts;
		texCoords = texes;
	}
}


