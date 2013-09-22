package core;

import view.AppView;

/**
 * @author Luki
 */
public class DemoApp {

	public static void main(String[] args) {

		Application.getInstance().init(args);
		Application.getInstance().loadConfigFile("pedviz.properties");

		Application.getInstance().addImage("pedigree", "res/pedigree.png");
		Application.getInstance().addImage("folder", "res/csvfile.png");
		Application.getInstance().addImage("delete", "res/delete.png");

		Application.getInstance().set(new AppController(), new AppView());
		Application.getInstance().start();

	}
}
