package view;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.*;


public class MainView extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		MainViewController mvc = new MainViewController();
		
		Scene scene = new Scene(mvc, 383, 620);
		Font.loadFont(MainView.class.getResource("Montserrat-Regular.ttf").toExternalForm(), 10);
		scene.getStylesheets().add(getClass().getResource("MainView.css").toExternalForm());
		primaryStage.setTitle("Welcome to uClear!");
		primaryStage.setMaxHeight(658);
		primaryStage.setMaxWidth(399);
		primaryStage.setMinHeight(primaryStage.getMaxHeight());
		primaryStage.setMinWidth(primaryStage.getMaxWidth());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

