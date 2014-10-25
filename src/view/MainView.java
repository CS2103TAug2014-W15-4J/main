package view;
	
import java.io.IOException;

import exception.TaskInvalidDateException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.*;


public class MainView extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException, TaskInvalidDateException {
		MainViewController mvc = new MainViewController();
		
		Scene scene = new Scene(mvc, 383, 620);
		Font.loadFont(MainView.class.getResource("Montserrat-Regular.ttf").toExternalForm(), 10);
		scene.getStylesheets().add(getClass().getResource("MainView.css").toExternalForm());
		primaryStage.setTitle("uClear");
		
		if (getOS().equals("Mac OS X")) {
			primaryStage.setMaxHeight(642);
			primaryStage.setMaxWidth(383);
		} else {
			primaryStage.setMaxHeight(658);
			primaryStage.setMaxWidth(399);
		}
		
		primaryStage.setMinHeight(primaryStage.getMaxHeight());
		primaryStage.setMinWidth(primaryStage.getMaxWidth());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private String getOS() {
		return System.getProperty("os.name");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

