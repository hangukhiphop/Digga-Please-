#version 120

uniform vec3 location;
uniform mat4x4 modelToWorld;
varying vec4 pos;

void main() 
{	
	pos = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = pos;	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
}