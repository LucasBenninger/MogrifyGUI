package benninger.lucas.mogrifygui.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import benninger.lucas.mogrifygui.Main;
import benninger.lucas.mogrifygui.logger.Logger;
import benninger.lucas.mogrifygui.zip.Zip;

public class MainView {
	public Main mainClass; //Link to the Main Class.
	public AnchorPane anchor = new AnchorPane();
	public static Logger logger = new Logger();
	public static Zip zip = new Zip();
	public double dist = 0D;
	public String imageMagickLocation;
	private static String OS = System.getProperty("os.name");
	private static String containingFolder = Main.containingFolder;
	private static String linuxExe = "/usr/bin/mogrify";
	private static String windowsExe = "C:\\Program Files\\ImageMagick-6.9.1-Q16" + File.separator + "mogrify.exe";
	private static String logFile = containingFolder + "error.log";
	private String reducedFolder = "ReducedQuality";

	// UI Elements
	public Label folderLocationLabel;
	public TextField folderLocationTextField;
	public Label qualityLabel;
	public Button folderSelection;
	public Slider slider;
	public Button compressButton;
	public CheckBox autoCompressToggle;
	public HBox hbox = new HBox(40);
	public VBox vboxLeft = new VBox(5);
	public VBox vboxRight = new VBox(5);

	public MainView(Stage primaryStage, Main theMainClass) {
		mainClass = theMainClass;
		System.out.println(containingFolder);
		constructWindow();
	}

	public void constructWindow() {
		dist += 15;

		//Program Name
		folderLocationLabel = new Label("Folder Location:");
		AnchorPane.setTopAnchor(folderLocationLabel, dist);
		AnchorPane.setLeftAnchor(folderLocationLabel, 15D);
		dist += 20;

		folderLocationTextField = new TextField();
		AnchorPane.setTopAnchor(folderLocationTextField, dist);
		AnchorPane.setLeftAnchor(folderLocationTextField, 15D);
		AnchorPane.setRightAnchor(folderLocationTextField, 15D);
		dist += 40;

		//Generate Button
		Button folderSelection = new Button("Open Folder Location");
		AnchorPane.setTopAnchor(folderSelection, dist);
		AnchorPane.setRightAnchor(folderSelection, 15D);

		anchor.getChildren().add(folderSelection);

		folderSelection.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File selectedDirectory = directoryChooser.showDialog(null);

				if (selectedDirectory == null) {
					folderLocationTextField.setText("No Directory selected");
				} else {
					folderLocationTextField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});
		dist += 20;
		mainClass.height = (int) (dist + 40);

		anchor.getChildren().addAll(folderLocationLabel, folderLocationTextField);

		//Quality

		qualityLabel = new Label("Quality:");
		AnchorPane.setTopAnchor(qualityLabel, dist);
		AnchorPane.setLeftAnchor(qualityLabel, 15D);
		dist += 20;

		//execTextField = new TextField();
		slider = new Slider(0.0, 100.0, 50.0);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		//slider.setMajorTickUnit(0.0f);
		slider.setBlockIncrement(1.0f);

		AnchorPane.setTopAnchor(slider, dist);
		AnchorPane.setLeftAnchor(slider, 15D);
		AnchorPane.setRightAnchor(slider, 15D);
		dist += 40;

		anchor.getChildren().addAll(qualityLabel, slider);

		//Compress Button
		compressButton = new Button("Reduce Quality");

		AnchorPane.setTopAnchor(compressButton, dist);
		AnchorPane.setLeftAnchor(compressButton, 15D);
		AnchorPane.setRightAnchor(compressButton, 15D);
		dist += 40;
		anchor.getChildren().addAll(compressButton);

		compressButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				compressButton.setText("Compressing...");
				logger.log("copyImages Method Call");
				copyImages();
				logger.log("buttonAction Method Call");
				buttonAction();
			}
		});

		//Compress Button
		autoCompressToggle = new CheckBox();
		autoCompressToggle.setText("Automatically Compress");
		autoCompressToggle.setSelected(true); //Default to auto-compress

		AnchorPane.setTopAnchor(autoCompressToggle, dist);
		AnchorPane.setLeftAnchor(autoCompressToggle, 15D);
		AnchorPane.setRightAnchor(autoCompressToggle, 15D);
		dist += 40;
		anchor.getChildren().addAll(autoCompressToggle);

		mainClass.height = (int) (dist + 40);
	}

	private void buttonAction() {
		if (OS.contains("Linux")) {
			logger.log("Detected OS as Linux");
			File f = new File(linuxExe);
			if (f.exists() && !f.isDirectory()) {
				logger.log("Found mogrify executable at "+linuxExe);
				try {
					String toExecute = f.toString() + " -quality " + slider.getValue() + " " + folderLocationTextField.getText() + File.separator + reducedFolder + File.separator + "*.jpg";
					logger.log("Executing the following:");
					logger.log(toExecute);
					Runtime r = Runtime.getRuntime();
					Process p = r.exec(toExecute);
					p.waitFor();
					BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";

					while ((line = b.readLine()) != null) {
						logger.log(line);
					}
					b.close();
					if (autoCompressToggle.isSelected()) {
						logger.log("auto compression enabled");
						zip.createZip(folderLocationTextField.getText() + File.separator + reducedFolder + File.separator, "Compilation.zip");
					}else
						logger.log("auto compression disabled");
					compressButton.setText("Finished!");
				} catch (Exception e) {
					logger.dumpLog(e.toString(), logFile);
				}

			} else {
				compressButton.setText("Reduce Quality");
				logger.dumpLog("Unable to find " + linuxExe, logFile);
				//Display Warning!
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning!");
				alert.setHeaderText("Could not find the ImageMagick Executable");
				alert.setContentText("Expected Location: " + linuxExe);
				alert.show();
			}
		} else if (OS.contains("Windows")) {
			logger.log("Detected OS as Windows");
			File f = new File(windowsExe);
			if (f.exists() && !f.isDirectory()) {
				logger.log("Found mogrify at "+windowsExe);
				try {
					logger.log("Getting file list in Reduced Quality image Dir");
					File imageFolder = new File(folderLocationTextField.getText() + File.separator + reducedFolder);
					File[] fileList = imageFolder.listFiles();

					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].toString().endsWith(".jpg") || fileList[i].toString().endsWith(".png") || fileList[i].toString().endsWith(".jpeg")) {
							String toExecute = "mogrify.exe -quality " + slider.getValue() + " " + fileList[i];
							ProcessBuilder pb = new ProcessBuilder("mogrify.exe", "-quality", String.valueOf(slider.getValue()), fileList[i].toString());
							logger.log("Executing the following:");
							logger.log(toExecute);

							pb.inheritIO();
							pb.redirectErrorStream();
							pb.directory(new File("C:\\Program Files\\ImageMagick-6.9.1-Q16"));
							Process p = pb.start();
							try (InputStream is = p.getInputStream()) {
								int in = -1;
								while ((in = is.read()) != -1) {
									logger.logf(String.valueOf((char)in));
								}
							}
							logger.log("Exited with " + p.waitFor());
						}
					}
					if (autoCompressToggle.isSelected()) {
						logger.log("Auto Compression enabled");
						zip.createZip(folderLocationTextField.getText() + File.separator + reducedFolder + File.separator, "Compilation.zip");
					}else
						logger.log("Auto Compression disabled");
					compressButton.setText("Finished!");
				} catch (Exception e) {
					logger.dumpLog(e.toString(), logFile);
				}
			} else {
				compressButton.setText("Reduce Quality");
				logger.dumpLog("Unable to find " + windowsExe, logFile);
				//Display Warning!
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning!");
				alert.setHeaderText("Could not find the ImageMagick Executable");
				alert.setContentText("Expected Location: " + windowsExe);
				alert.show();
			}
		}
	}

	private void copyImages() {
		File newFolder = new File(folderLocationTextField.getText() + File.separator + reducedFolder);
		if (newFolder.exists() && newFolder.isDirectory()) {
			logger.log(reducedFolder+" Exists");
			//Remove old files
			logger.log("remove old files");
			File[] oldFiles = (new File(newFolder.toString()).listFiles());
			for (int i = 0; i < oldFiles.length; i++) {
				if (oldFiles[i].isFile()) {
					logger.log("removing file: "+oldFiles[i].toString());
					oldFiles[i].delete();
				}
			}

			//Prepare to copy over the new images
			File file = new File(folderLocationTextField.getText());
			File[] fileList = file.listFiles();

			for (int i = 0; i < fileList.length; i++) {
				//Remove path from file name
				String fileName = fileList[i].getPath().toString();
				String fileNameFix = "";
				String path = "";
				for (int j = fileName.length() - 1; j >= 0; j--) {
					if (String.valueOf(fileName.charAt(j)).equals("/") || String.valueOf(fileName.charAt(j)).equals("\\")) {
						fileNameFix = fileName.substring(j + 1, fileName.length());
						path = fileName.substring(0, j);
						j = -1; //Exit the loop.
					}
				}
				try {
					Files.copy(fileList[i].toPath(), new File(path + File.separator + reducedFolder + File.separator + fileNameFix).toPath());
					new File(path + File.separator + reducedFolder + File.separator + reducedFolder).delete(); //Remove self copied folder
				} catch (Exception e) {
					logger.dumpLog(e.toString(), logFile);
				}
			}

		} else if (newFolder.exists() && newFolder.isFile()) {
			logger.dumpLog("File exists in place of ReducedQuality Folder!", logFile);
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("A file called ReducedQuality is in the way!");
			alert.setTitle("A file in the folder you selected uses the same name as the folder used for the new images!");
			alert.show();
		} else if (!newFolder.exists()) {
			logger.log(reducedFolder+" doesn't exist; creating");
			File folder = new File(newFolder.toString());
			folder.mkdirs();
			copyImages(); //Return to check
		}
	}

}