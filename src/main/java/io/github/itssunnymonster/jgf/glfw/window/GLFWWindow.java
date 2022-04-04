//
// Copyright 2022 ItsSunnyMonster
//

package io.github.itssunnymonster.jgf.glfw.window;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class GLFWWindow implements AutoCloseable {
	private long handle;
	
	private boolean active = false;
	
	private static List<GLFWWindow> windows = new ArrayList<>();
	
	public GLFWWindow(GLFWWindowProps props) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, props.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, props.openGLVersionMajor);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, props.openGLVersionMinor);
		if (props.openGLCoreProfile) GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		handle = GLFW.glfwCreateWindow(props.width, props.height, props.title, NULL, NULL);
		if (handle == NULL) {
			throw new RuntimeException("Unable to create window!");
		}
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			
			GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
			
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			
			GLFW.glfwSetWindowPos(
					handle,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		GLFW.glfwMakeContextCurrent(handle);
		if (props.vsync) GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(handle);
		GL.createCapabilities();
		
		active = true;
		windows.add(this);
	}
	
	public long getHandle() {
		return handle;
	}

	public static int getWindowCount() {
		return windows.size();
	}
	
	public boolean update() {
		assert active;
		GLFW.glfwSwapBuffers(handle);
		return !GLFW.glfwWindowShouldClose(handle);
	}
	
	public static void pollEvents() {
		GLFW.glfwPollEvents();
	}

	public static void updateAll() {
		for (Iterator<GLFWWindow> winIt = windows.iterator(); winIt.hasNext();) {
			GLFWWindow window = winIt.next();
			if (!window.update()) { 
				window.close();
				winIt.remove();
			}
		}
		pollEvents();
	}

	@Override
	public void close() {
		GLFW.glfwDestroyWindow(handle);	
		active = false;
	}
	
	public static void cleanUpGLFW() {
		GLFW.glfwTerminate();
	}
}
