package io.github.itssunnymonster.jgf.app;

public class JGFAppManager {
	
	public static void run(JGFApp app, String[] args) {
		try {
			int exitCode = app.entry(args);
			System.out.println("=======================================================");
			if (exitCode != 0) {
				System.err.println("Application exited with none 0 code " + exitCode);
			} else {
				System.out.println("Application exited with code " + exitCode);
			}
		} catch (Exception e) { 
			System.out.println("=======================================================");
			System.err.println("Application threw an exception: " + e.getMessage());
			System.err.println("Application exited with none 0 code -100");
		}
	}
}
