package view;
	
import java.io.IOException;

import exception.TaskInvalidDateException;
import exception.TaskNoSuchTagException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.*;

//@author A0119414L
/**
 * This class is to display the view.
 * It is the interface between uClear and user.
 * 
 * @author Wang Zhipeng
 *
 */
public class MainView extends Application {
	
	//@author A0119414L
	@Override
	/**
	 * Set the window.
	 */
	public void start(Stage primaryStage) throws IOException, TaskInvalidDateException, TaskNoSuchTagException {
		MainViewController mvc = new MainViewController();
		
		Scene scene = new Scene(mvc, 900, 620);
		Font.loadFont(MainView.class.getResource("Roboto-Regular.ttf").toExternalForm(), 10);
		Font.loadFont(MainView.class.getResource("Roboto-Bold.ttf").toExternalForm(), 10);
		
		scene.getStylesheets().add(getClass().getResource("MainView.css").toExternalForm());
		primaryStage.setTitle("uClear");
		primaryStage.getIcons().add(new Image("/view/uClear-icon.png"));
		
		if (getOS().equals("Mac OS X")) {
			primaryStage.setMaxHeight(642);
			primaryStage.setMaxWidth(900);
		} else {
			primaryStage.setMaxHeight(658);
			primaryStage.setMaxWidth(916);
		}
		
		primaryStage.setMinHeight(primaryStage.getMaxHeight());
		primaryStage.setMinWidth(primaryStage.getMaxWidth());
		primaryStage.centerOnScreen();
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	//@author A0119414L
	/**
	 * 
	 * @return	Return the name of OS.
	 */
	private String getOS() {
		return System.getProperty("os.name");
	}
	
	//@author A0119414L
	/**
	 * 
	 * @param args	Arguments for execution
	 */
	public static void main(String[] args) {
		launch(args);
	}
}

