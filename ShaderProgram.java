import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


public abstract class ShaderProgram 
{
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	//private int modelToWorldID;
	
	public ShaderProgram(String vertexFile, String fragmentFile)
	{
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID,  fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
	}
	
	public void start(float x, float z)
	{
		GL20.glUseProgram(programID);
		int loc = GL20.glGetUniformLocation(programID, "location");	
		GL20.glUniform3f(loc, x, 0, z);		
	}
	
	public void setModelToWorldMatrix(Position worldPos, float scale)
	{
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
		Matrix4f.translate(new Vector3f(worldPos.X(), 0, worldPos.Z()), modelMatrix, modelMatrix);
		Matrix4f.rotate((float)Math.toRadians(worldPos.R()), new Vector3f(0, 1, 0), modelMatrix, modelMatrix);	
		FloatBuffer matrix4Buffer = BufferUtils.createFloatBuffer(16);
		modelMatrix.store(matrix4Buffer);
		matrix4Buffer.flip();
		int modelToWorldID = GL20.glGetUniformLocation(programID, "modelToWorld");
		GL20.glUniformMatrix4(modelToWorldID, false, matrix4Buffer);
	}
	
	public void stop()
	{
		GL20.glUseProgram(0);
	}
	
	public void cleanUp()
	{
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName)
	{
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	private static int loadShader(String file, int type)
	{
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader reader;
		try 
		{
			reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
			{
				shaderSource.append(line).append('\n');
			}
			reader.close();
		}  
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID,  GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.out.println(GL20.glGetShaderInfoLog(shaderID,  500));
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderID;		
	}
}

