import java.io.Serializable;


public class Position implements Serializable
{
	private String obj;
	private float x;
	private float z;
	private float rotation;
	
	public Position(float x, float z, float r)
	{
		this.x = x;
		this.z = z; 
		this.rotation = r;
	}
	
	public Position(String obj, float x, float z, float r)
	{
		this.obj = obj;
		this.x = x;
		this.z = z; 
		this.rotation = r;
	}
	
	
	public void change(float dx, float dz, float dr)
	{
		this.x += dx;
		this.z += dz;
		this.rotation += dr;
	}
	
	public void set(float x, float z, float r)
	{
		this.x = x;
		this.z = z;
		this.rotation = r;
	}
	
	public float distance2D(Position b)
	{
		return (float)Math.sqrt((this.x - b.X())*(this.x - b.X()) + (this.z - b.Z())*(this.z - b.Z()));
	}
	
	public String OBJ()
	{
		return obj;
	}
	
	public float X()
	{
		return x;
	}
	
	public float Z()
	{
		return z;
	}
	
	public float R()
	{
		return rotation;
	}
	
	public void setR(float R)
	{
		rotation = R;
	}
	
}
