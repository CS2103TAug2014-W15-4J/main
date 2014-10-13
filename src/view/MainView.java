package view;

import java.io.IOException;

//import javafx.scene.Parent;
//import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * Main view (GUI) of uClear
 * 
 * @author Wang Zhipeng
 *
 */

public class MainView extends Application {
	
	MainViewController mainViewControl;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
//		Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
		
		mainViewControl = new MainViewController();
		mainViewControl.setText("Enter Command here...");
		mainViewControl.setDateLabel();
        
//      Scene scene = new Scene(root, 434, 620);
		Scene scene = new Scene(mainViewControl);
		primaryStage.setWidth(450); 
		primaryStage.setHeight(620); 
        
        primaryStage.setTitle("uClear: Welcome!");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(MainView.class.getResource("MainView.css").toExternalForm());
        primaryStage.show();
		
	}
	
	public MainViewController getController() {
		return this.mainViewControl;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
