 package renderEngine;

import java.util.List;
import java.util.Map;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer {
	

		
	// Set-up the projection matrix
	//private Matrix4f projectionMatrix;
	private StaticShader shader;
	
	// Pass-in the static shader because we want to load the projection matrix
	// straight away to the shader
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
		// set the shader so that we can access it whenever
		// we want
		this.shader = shader;

		// This call is in the constructor as it only needs to be
		// set-up only once as it's not going to change
		//createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		// 
	}
	

	
	// This new render method takes in a map and list of entities
	public void render(Map<TexturedModel,List<Entity>> entities){
		for(TexturedModel model:entities.keySet()){
			// Loop through each of the keys
			// and for each texturedModel we
			// first need to prepare the textured model
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			// Then for each entitiy in the batch...
			for(Entity entity:batch){
				prepareInstance(entity);
				// final render
				// Now go ahead and render the object
				// First parameter specifies what we want to render
				// Then where in the data it should start rendering from
				// Then it needs to know how many vertices to render
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model){
		// It does still need access to the RawModel so extract it. It will be used for
		// binding the vertex array, etc
		RawModel rawModel = model.getRawModel();
		
		// Firstly we need to bind the array, using the vaoID from the model object
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		// Then we need to active the attribute list in which our data is stored
		GL20.glEnableVertexAttribArray(0);
		
		// Now enable buffer 1 for the texture coordinates before rendering
		GL20.glEnableVertexAttribArray(1);
		
		// Now enable buffer 2 for the normals before rendering
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = model.getTexture();
		// Now we have the shine variables, load them to the shader
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		// Now we need to tell OpenGL which texture we want to render onto our quad
		// We will therefore put it into one of the texture banks that OpenGL provides
		// The first bank is where the Sampler2DUniform defaults to zero
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		// Now bind the texture to it
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	// This method unbinds the texturedModel once we've finished rendering all
	// the entities that use that texturedModel
	private void unbindTexturedModel(){
		// Now we've finished using everything we need to disable that attribute list
		GL20.glDisableVertexAttribArray(0);
		
		// Disable the texture buffer
		GL20.glDisableVertexAttribArray(1);
		
		// Disable the normals buffer
		GL20.glDisableVertexAttribArray(2);
		
		// Then unbind the VAO
		GL30.glBindVertexArray(0);
	}
	
	// A mehod that prepares the entities of each textured model
	private void prepareInstance(Entity entity){
		// Now we want to load the Entity's transformation
		// to the Vertex shader to the matrix uniform variable so that
		// it moves where the model is rendered on-screen
		// The following creates a Matrix from our Maths class
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		// Now we need to load the transformation matrix to the shader
		// using our method in the Shader class
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	
/*	// This method will actually render the raw model. 
	// It takes in the model that should be rendered
	// NB: Note this has been changed to take in a TexturedModel, from RawModel
	// NB: Now changed to take an Entity, rather than TexturedModel
	// The shader is also passed-in so that we can upload the entity's transformation and
	// have it rendered in a different position
	public void render(Entity entity, StaticShader shader){
		
		// Still need to access the TexturedModel so we extract it from the Entity
		TexturedModel model = entity.getModel();
		
		// It does still need access to the RawModel so extract it. It will be used for
		// binding the vertex array, etc
		RawModel rawModel = model.getRawModel();
		
		// Firstly we need to bind the array, using the vaoID from the model object
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		// Then we need to active the attribute list in which our data is stored
		GL20.glEnableVertexAttribArray(0);
		
		// Now enable buffer 1 for the texture coordinates before rendering
		GL20.glEnableVertexAttribArray(1);
		
		// Now enable buffer 2 for the normals before rendering
		GL20.glEnableVertexAttribArray(2);
		
		// Now we want to load the Entity's transformation
		// to the Vertex shader to the matrix uniform variable so that
		// it moves where the model is rendered on-screen
		// The following creates a Matrix from our Maths class
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		// Now we need to load the transformation matrix to the shader
		// using our method in the Shader class
		shader.loadTransformationMatrix(transformationMatrix);
		
		// Now load values relating to the objects shine
		ModelTexture texture = model.getTexture();
		// Now we have the shine variables, load them to the shader
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		// Now we need to tell OpenGL which texture we want to render onto our quad
		// We will therefore put it into one of the texture banks that OpenGL provides
		// The first bank is where the Sampler2DUniform defaults to zero
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		// Now bind the texture to it
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		
		// Now go ahead and render the object
		// First parameter specifies what we want to render
		// Then where in the data it should start rendering from
		// Then it needs to know how many vertices to render
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		// Now we've finished using everything we need to disable that attribute list
		GL20.glDisableVertexAttribArray(0);
		
		// Disable the texture buffer
		GL20.glDisableVertexAttribArray(1);
		
		// Disable the normals buffer
		GL20.glDisableVertexAttribArray(2);
		
		// Then unbind the VAO
		GL30.glBindVertexArray(0);
	}*/
	

	

}
