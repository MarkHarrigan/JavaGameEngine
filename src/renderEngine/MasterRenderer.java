package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

public class MasterRenderer {
	
	// We will require some constants that will be used in the projection matrix
	private static final float FOV = 70;
	// Set up the near-plane variable (plane closest to the camera)
	private static final float NEAR_PLANE = 0.1f;
	// Set up the far-plane value (farthest plane from the camera)
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	// This class will handle all of the render code in our game
	// It will first need its own instance of a static shader
	private StaticShader shader = new StaticShader();
	// and a renderer to do the rendering
	private EntityRenderer renderer;
	
	// Create a hashmap that contains all of the textured models 
	// and their respective entities that need to be rendered for
	// each particular frame.
	// The hashmap will contain a load of model keys and each of them will be
	// mapped to a list of entities that use that specific texturedmodel 
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

	public MasterRenderer(){
		// Ensure that triangles pointing away from 
		// the camera are not rendered
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
	}
	
	// Render method called once per frame to render all
	// entities in the scene
	public void render(Light sun, Camera camera){
		prepare();
		shader.start();
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		// clear the hashmap of entities
		// otherwise they will build-up each
		// frame and you'll end up rendering millions...
		entities.clear();
	}
	
	// All of the entities need to be sorted into the correct list
	public void processEntity(Entity entity){
		// Get the model that the entity uses
		TexturedModel entityModel = entity.getModel();
		// Get the list that corresponds to that entity
		List<Entity> batch = entities.get(entityModel);
		// Check if the batch already exists
		if(batch!=null){
			batch.add(entity);
		} else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	// The shader always needs to be cleaned-up when we close the game
	public void cleanUp(){
		shader.cleanUp();
	}
	
	// This method will be called once every frame to prepare OpenGL to render the game
	public void prepare(){
		// Test which triangles are in front of each other
		// to ensure it gets rendered in the correct order
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// The we need to clear the colour from the last frame
		// '1' is the maximum for each RGB value
		// and also clear the depth buffer every single frame
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);	
	}
	
	// Method to create the projection matrix
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
	}
}
