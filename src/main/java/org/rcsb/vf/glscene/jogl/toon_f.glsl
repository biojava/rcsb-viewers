varying vec3 normal;
	
	void main()
	{
		float intensity;
		vec4 color;
		vec3 n = normalize(normal);
		vec3 position = vec3(0.0, 0.0, 1.0);
		
		intensity = dot(position,n);
		
		if (intensity > 0.95)
			color = vec4(gl_FrontMaterial.ambient.x,gl_FrontMaterial.ambient.y,gl_FrontMaterial.ambient.z,1.0);
		else if (intensity > 0.5)
			color = vec4(gl_FrontMaterial.ambient.x - .15,gl_FrontMaterial.ambient.y - .15,gl_FrontMaterial.ambient.z - .15,1.0);
		else if (intensity > 0.25)
			color = vec4(gl_FrontMaterial.ambient.x - .25,gl_FrontMaterial.ambient.y - .25,gl_FrontMaterial.ambient.z - .25,1.0);
		//else if (intensity < 0.05 && intensity > -0.05)
			//color = vec4(0.0,0.0,0.0,0.0);
		else 
			color = vec4(gl_FrontMaterial.ambient.x - .4,gl_FrontMaterial.ambient.y - .4,gl_FrontMaterial.ambient.z - .4,1.0);
		

		
		gl_FragColor = color;
	} 