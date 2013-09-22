package core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.media.j3d.Canvas3D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import view.AppView;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class Application {

	private static Application instance = null;

	private HashMap<String, Boolean> flags;

	private AppController controller;

	private AppView view;

	private boolean j3dInstalled = true;

	private HashMap<String, ImageIcon> images;

	private Properties properties;

	private String version = "(Version 2008-02-15)";

	private Application() {
		images = new HashMap<String, ImageIcon>();
		flags = new HashMap<String, Boolean>();
		j3dInstalled = testJ3D();
	}

	public static Application getInstance() {
		if (instance != null) {
			return instance;
		}

		instance = new Application();
		return instance;
	}

	public void init(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e);
		}

		for (String flag : args) {
			if (flag.startsWith("-")) {
				flags.put(flag.substring(1), true);
			}
		}
	}

	public void loadConfigFile(String filename) {
		try {
			properties = new Properties();
			FileInputStream stream = new FileInputStream(filename);
			properties.load(stream);
		} catch (Exception e) {
			Application.getInstance().handleException(e);
		}
		;
	}

	public String getProperty(String property) {
		return properties.getProperty(property);
	}

	public boolean isSet(String flag) {
		Boolean value = flags.get(flag);
		if (value == null) {
			return false;
		} else {
			return value;
		}
	}

	public String toString() {
		String result = "";
		if (!flags.keySet().isEmpty()) {
			result += "Settings:\n";
			for (String flag : flags.keySet()) {
				result += flag + "\n";
			}
		}
		return result;
	}

	public AppController getController() {
		return controller;
	}

	public void setController(AppController controller) {
		this.controller = controller;
	}

	public AppView getView() {
		return view;
	}

	public void set(AppController controller, AppView view) {
		this.view = view;
		this.controller = controller;
		controller.setView(view);

	}

	public void start() {
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
	}

	public void addImage(String name, String filename) {
		images.put(name, new ImageIcon(filename));
	}

	public ImageIcon getImage(String name) {
		return images.get(name);
	}

	/**
	 * checks if java3d is intalled
	 * 
	 * @return
	 */
	private boolean testJ3D() {
		boolean result = true;
		try {
			new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		} catch (Throwable e) {
			JOptionPane
					.showMessageDialog(
							null,
							"Java3D isn't installed or the graphic card driver has to be updated.\n Download it from http://java.sun.com/products/java-media/3D",
							"Error", JOptionPane.ERROR_MESSAGE);
			result = false;
		}
		return result;
	}

	public boolean isJ3dInstalled() {
		return j3dInstalled;
	}

	public void handleException(Exception e) {
		String debugging = getInstance().getProperty("pedvizapi.debugging");
		if (debugging != null && debugging.equals("yes")) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "Error:\n\n" + e.getMessage(),
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public String getVersion() {
		return version;
	}
}
