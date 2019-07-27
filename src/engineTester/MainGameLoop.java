package engineTester;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
	
/*		// Create a new Static Shader for use when rendering
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);	*/
		
		RawModel model = OBJLoader.loadObjModel("stall", loader);
		
		//ModelTexture texture = new ModelTexture(loader.loadTexture("white"));
		
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("stallTexture")));
		// Get the texture so that we can set the shine variables to it.
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		Entity entity = new Entity(staticModel, new Vector3f(0,-2,-50),0,0,0,1);
		Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
		
		Camera camera = new Camera();
		
		// Create the MasterRenderer
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
			
			entity.increaseRotation(0, 0, 0);
			
			// Now increase the position each frame...
			// entity.increasePosition(0.002f, 0, 0);
			// Now increase the z-position
			// entity.increasePosition(0, 0, -0.1f);
			// Move the camera every frame
			// Call the method to take in the key presses
			camera.move();

			renderer.processEntity(entity);
			// Once per frame, 
			renderer.render(light, camera);
			// and rotate it each frame...
			// entity.increaseRotation(0, 1, 0);
			// put all game logic here
/*			renderer.prepare();
			
			// before we render we should start the shader
			shader.start();
			
			// Load the light each frame so that it can be
			// manipulated and update the shader
			shader.loadLight(light);
			
			// Load the viewMatrix every frame
			shader.loadViewMatrix(camera);*/
/*			renderer.render(entity, shader);
			
			// now the rendering has finished we can stop the shader
			shader.stop();*/

			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
//		shader.cleanUp();
		
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
