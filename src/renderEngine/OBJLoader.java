package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class OBJLoader {

		private static List<Vector3f> verticesList;

		public static RawModel loadObjModel(String fileName, Loader loader){
			
			FileReader fr = null;
			
			try {
				fr = new FileReader(new File("res/"+fileName+".obj"));
			} catch (FileNotFoundException e) {
				System.err.println("Could not load OBJ file.");
				e.printStackTrace();
			}
			
			// Create a BufferedReader object to allow us to read from the above file
			BufferedReader reader = new BufferedReader(fr);
			
			String line; // For reading in each line of the file
			
			// List to hold vertices
			List<Vector3f> vertices = new ArrayList<Vector3f>();
			// Now a Vector2f to hold the texture coords
			List<Vector2f> textures = new ArrayList<Vector2f>();
			// Then another Vector3f for the normal vectors
			List<Vector3f> normals = new ArrayList<Vector3f>();
			// Next a list of integers for the indices
			List<Integer> indices = new ArrayList<Integer>();
			
			// All the data will eventually be in float arrays for the loader
			float[] verticesArray = null;
			float[] normalsArray = null;
			float[] textureArray = null;
			int[] indicesArray = null;
			
			// Wrap everything in a try-catch statement
			try{
				
				// infinite loop to read through file until we break out
				while(true){
					line = reader.readLine();
					// Split the line at a space
					String[] currentLine = line.split(" ");
					// Check to see if the line begins with a 'v', i.e. vertex
					if(line.startsWith("v ")){
						Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
								Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
						vertices.add(vertex);
					} else if(line.startsWith("vt ")){ // texture coord
						Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), 
								Float.parseFloat(currentLine[2]));
						textures.add(texture);
					} else if(line.startsWith("vn ")){
						Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
								Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));						
						normals.add(normal);
					} else if(line.startsWith("f ")){
						// If the line begins with 'f' then we know that
						// we have moved on to the next section of the obj file
						// we've read in all the data from the vertices, textures
						// and normals - we have all the data we need.
						// We can now set-up arrays now we know the sizes
						textureArray = new float[vertices.size()*2];
						normalsArray = new float[vertices.size()*3];
						// ...and break out of the loop
						break;
						
					}
				}
				
				// Now we enter another while loop to go through all those base lines
				while(line!=null){
					if(!line.startsWith("f ")){
						line = reader.readLine();
						continue;
					}
					// If the line DOES begin with 'f' then it's a face so we need to
					// manipulate the string. Break the line up into three parts, one
					// for each vertex. That will give us a string array that starts 
					// with 'f' and then has the three vertices.
					String[] currentLine = line.split(" ");
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");
					
					// Call the method for each of the vertices in the triangle
					// we're processing. This will ensure that the texture and
					// normal data is sorted into their correct positions
					processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
					processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
					processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
					
					// Each loop we have to read the next line
					line = reader.readLine();
				}
				
			} catch(Exception e){
				e.printStackTrace();
			}
			
			// At the end of the file we have to close the reader
			
			// We need to convert the vertex list into a float array
			verticesArray = new float[vertices.size()*3];
			// We also have to convert the indices list into a int array
			indicesArray = new int[indices.size()];
			
			// Now copy across all the data
			// for each vertex, put data into array
			int vertexPointer = 0;
			for(Vector3f vertex:vertices){
				verticesArray[vertexPointer++] = vertex.x;
				verticesArray[vertexPointer++] = vertex.y;
				verticesArray[vertexPointer++] = vertex.z;
			}
			
			// Now we need top copy across the indices data from the indices list
			// into the indices array
			for(int i=0; i<indices.size(); i++){
				indicesArray[i] = indices.get(i);
			}
			
			return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
			
		}
		
		// Method to process each vertex
		private static void processVertex(String[] vertexData, 
					List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, 
					float[] textureArray, float[] normalsArray){
			
			// The data for each vertex. We want to put these into the correct positions
			// The first bit of data will tell us the index of the vertex position in the
			// vertex position list. We need to minus one as the OBJ file starts at one
			// but our arrays start at zero.
			int currentVertexPointer = Integer.parseInt(vertexData[0])-1;
			
			indices.add(currentVertexPointer);
			
			// Now we want to get the texture that corresponds to the vertex
			// The texture index is in the second bit of the vertex data (1) as array
			Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
			
			// Add the index to the texture array but in the same position as the vertex position
			// Multiply by 2 as each texture has two floats
			textureArray[currentVertexPointer*2] = currentTex.x;
			
			// We have to minus 1 the current y position because OpenGL starts from the 
			// top-left of the texture whereas Blender starts from the bottom-left.
			textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
			
			// Now we can get the normal vector that is associated with this vertex
			// the index for that is in element 2 of the data so we need to minus 1
			Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
			
			// So now we have the normal associated with this vertex so we need to put
			// it in the correct position in the array or normals
			normalsArray[currentVertexPointer*3] = currentNorm.x;
			normalsArray[currentVertexPointer*3+1] = currentNorm.y;
			normalsArray[currentVertexPointer*3+2] = currentNorm.z;
			
		}
		
		
}
