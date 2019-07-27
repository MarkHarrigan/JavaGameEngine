package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
//import java.util.List;







import models.RawModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Loader {
	
	// To enable the deletion of the VAO and VBO objects, we need to keep a record of their IDs
	private ArrayList<Integer> vaos = new ArrayList<Integer>();	
	private ArrayList<Integer> vbos = new ArrayList<Integer>();	
	private ArrayList<Integer> textures = new ArrayList<Integer>();
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		storeDataInAttributeList(2,3,normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new  FileInputStream("res/"+fileName+".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public void cleanUp(){
		
		// Iterate through the ArrayList and delete
		// each VAO by using its ID
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		
		// Do the same with the VBO list
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
		
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
		
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		// Create an empty VBO
		int vboID = GL15.glGenBuffers();
		// Bind the buffer to allow action on it
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// Now we can convert array into a float buffer
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// Now we can store the data into the VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// Now put the VBO into the VAO
		// First parameter is the number of the attribute list into which you want to store the data	
		// Then the length of each vertex
		// Then the type of data, in this case a float
		// Then whether the data is normalised or not
		// Then the distance between each of your vertices, i.e. any other data between them?
		// Then finally the offset, i.e. should it start at the beginning of the data?
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0,0);
		// Now we've finished using the VBO we can go ahead and remove the bind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
	}
	
	private void unbindVAO(){
		
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		// This method converts a float array into a float buffer
		// Create an empty float buffer with size of data
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// Put the data into the float buffer
		buffer.put(data);
		// At the moment the buffer is set-up for writing. We need to flip() the buffer to enable data to be read
		buffer.flip();
		// return the buffer for use
		return buffer;
	}
	

}
