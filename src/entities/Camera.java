package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

// This class is going to represent our virtual camera
public class Camera {

	// Set the camera's initial position
	private Vector3f position = new Vector3f(0,0,0);
	// Set the camera's initial pitch, yaw and roll
	private float pitch;
	private float roll;
	private float yaw;
	private float cameraSpeed = 0.6f;
	
	public Camera(){}
	
	// Include a move method that controls the camera's movement
	public void move(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			position.z-=cameraSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			position.z+=cameraSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			position.x-=cameraSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			position.x+=cameraSpeed;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

	public float getYaw() {
		return yaw;
	}
	
	
	
}
