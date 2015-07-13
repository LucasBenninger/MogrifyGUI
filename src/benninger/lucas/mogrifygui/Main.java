package benninger.lucas.mogrifygui;

/*
 * Author:  Lucas Benninger <benningerlucas@gmail.com>
 * Description: A basic GUI program for changing the quality of images in a folder
 */

import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import benninger.lucas.mogrifygui.view.MainView;

public class Main extends Application {
	public MainView mainView; //Link to MainView for creation of the UI
	public final double VERSION = 0.02;
	public String title = "MogrifyGUI - " + VERSION;
	public int height;
	public static int width = 350;
	public AnchorPane anchor = new AnchorPane();
	public double dist = 0D;
	public String fileContents;
	public static String containingFolder;

	@Override
	public void start(Stage primaryStage) {
		//Remove file name to have Folder name for log file
		if (!containingFolder.endsWith("/") || !containingFolder.endsWith("\\")) {
			for (int i = containingFolder.length() - 1; i >= 0; i--) {
				if (String.valueOf(containingFolder.charAt(i)).equals("/") || String.valueOf(containingFolder.charAt(i)).equals("\\")) {
					containingFolder = containingFolder.substring(0, i);
					i = -1; //Exit the loop.
				}
			}
		}

		primaryStage.setTitle(title);

		/* Set up the View */
		mainView = new MainView(primaryStage, this);
		anchor = mainView.anchor;

		//Scene scene = new Scene(anchor, width, height);
		Scene scene = new Scene(anchor, width, height);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	public static void main(String[] args) {
		//Get Jar Location
		try {
			containingFolder = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		launch(args);
	}

}