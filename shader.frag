#version 120
uniform vec3 location;
uniform sampler2D texture0;
varying vec4 pos;

void main()
{
		float dist = length(pos.xz - location.xz);			
		vec4 color;
		if(dist < 40)
		{
			color = texture2D(texture0, gl_TexCoord[0].xy);
		}
		else
		{
			color = vec4(0, 0, 0, 1);
		}
		
		gl_FragColor = color;
}
