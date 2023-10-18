#version 150
// Star Nest by Pablo Roman Andrioli
// License: MIT

uniform float GameTime;
uniform vec2 ScreenSize;
out vec4 fragColor;

#define speed  5.

void main() {
	vec2 uv=gl_FragCoord.xy/ScreenSize.xy-.5;
	uv.y*=ScreenSize.y/ScreenSize.x;
	vec3 dir=vec3(uv*0.234,1.);
	float time=GameTime*speed;

	float a1=.5/ScreenSize.x*2.;
	float a2=.8/ScreenSize.y*2.;
	mat2 rot1=mat2(cos(a1),-sin(a1),-sin(a1),cos(a1));
	mat2 rot2=mat2(-cos(a2),sin(a2),sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	vec3 from=vec3(1.,.5,-0.7);
	from+=vec3(time*3.3,from.x *time,-2.);
	from.xz*=rot1;
	from.xy*=rot2;
	
	float s=0.1,fade=2.;
	vec3 v=vec3(0.);
	for (int r=0; r<13; r++) {
		vec3 p=from+s*dir*.5;
		p = abs(vec3(0.666)-mod(p,vec3(0.734*2.))); 
		float pa,a=pa=0.;
		for (int i=0; i<13; i++) { 
			p=abs(p)/dot(p,p)-0.89;
			a+=abs(length(p)-pa); 
			pa=length(p);
		}
		float dm=max(0.,0.133-a*a*.001); 
		a*=a*a;
		if (r>11) fade*=1.-dm; 
		v+=vec3(dm,dm*.5,0.);
		v+=fade;
		v+=vec3(s,s*s,s*s*s*s)*a*0.0015*fade; 
		fade*=0.566; 
		s+=0.25;
	}
	v=mix(vec3(length(v)),v,0.850); 
	fragColor = vec4(v*.01,1.);	

}

