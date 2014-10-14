package view;

import java.io.IOException;

//import javafx.scene.Parent;
//import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main view (GUI) of uClear
 * 
 * @author Wang Zhipeng
 *
 */

public class MainView extends Application {
	
	MainViewController mainViewControl;
	
	final static Logger logForMainView = Logger.getLogger(MainView.class.getName()); 
	
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
		logForMainView.log(Level.INFO, "Set size of window successfully!");
        
        primaryStage.setTitle("uClear: Welcome!");
        logForMainView.log(Level.INFO, "Set title of window successfully!");
        
        primaryStage.setScene(scene);
        logForMainView.log(Level.INFO, "Set scene of window successfully!");
        
        scene.getStylesheets().add(MainView.class.getResource("MainView.css").toExternalForm());
        primaryStage.show();
		
	}
	
	public MainViewController getController() {
		return this.mainViewControl;
	}

	public static void main(String[] args) {
		launch(args);
		logForMainView.log(Level.INFO, "Set window successfully!");
	}

}
