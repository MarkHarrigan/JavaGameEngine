package renderEngine;

/***********************************************************************************
 * This is the main Display Manager of our first OpenGL application
 **********************************************************************************/
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 720;
	private static final int MAX_FPS = 120;
	
	public static void createDisplay(){
		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(SCREEN_WIDTH,SCREEN_HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("My first Java game");
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	
	public static void updateDisplay(){
		
		Display.sync(MAX_FPS);
		Display.update();
		
	}
	
	public static void closeDisplay(){
		
		Display.destroy();
		
	}
	

}
