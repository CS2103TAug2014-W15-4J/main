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
//		mainViewControl.display();
		
        
//        Scene scene = new Scene(root, 434, 620);
		Scene scene = new Scene(mainViewControl);
		primaryStage.setWidth(450); //<fx:root type="javafx.scene.layout.VBox" xmlns:fx="http://javafx.com/fxml"></fx:root>
		primaryStage.setHeight(620); //<TextField fx:id="textField"/><Button text="Click Me" onAction="#doSomething"/>
        
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
