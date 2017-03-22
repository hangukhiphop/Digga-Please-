
public class StaticShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "Shaders/shader.vert";
	private static final String FRAGMENT_FILE = "Shaders/shader.frag";
	
	public StaticShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");		
		super.bindAttribute(1, "in_Color");
		super.bindAttribute(2, "in_TextureCoord");
	}
}
