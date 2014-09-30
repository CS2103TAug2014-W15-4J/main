package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class MainView extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		//Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
		
		MainViewController mainViewControl = new MainViewController();
		mainViewControl.setText("Hello!");
        
        //Scene scene = new Scene(root, 434, 620);
		Scene scene = new Scene(mainViewControl);
		primaryStage.setWidth(434);
		primaryStage.setHeight(600);
        
        primaryStage.setTitle("uClear: Welcome!");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(MainView.class.getResource("MainView.css").toExternalForm());
        primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
