package view;

import controller.Logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainViewController extends VBox {
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			final Calendar cal = Calendar.getInstance();
			date.setText(dateFormat.format(cal.getTime()));
		}
	}));
	
	private static String input;
	
    @FXML 
    private TextField textField;
    
    @FXML
    private Label date;
    
    @FXML
    private VBox toBeCompleted;
    
    @FXML
    private VBox toDo;
    
    private ArrayList<String> toDoTaskList;
    
    private ArrayList<String> toBeCompletedTaskList;

    public MainViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        toDoTaskList = new ArrayList<String>();
        toBeCompletedTaskList = new ArrayList<String>();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }
    
    public String getInput() {
    	return textField.getText();
    }
    
    public void display() {
    	Label displayForToBeCompleted = new Label();
    	Label displayForToDo = new Label();
    	input = getInput();
    	Logic.initialize();
    	String feedback = Logic.readAndExecuteCommands(input);
//    	System.out.println(input);
    	
    	displayForToBeCompleted.setText(feedback);
    	displayForToDo.setText(Logic.getTaskList().getList().toString());
    	toBeCompleted.getChildren().add(displayForToBeCompleted);
    	toDo.getChildren().add(displayForToDo);
    }
    
    public String getCommand() {
    	return input;
    }
    
    @FXML
    public void onEnter() {
    	display();
    	textField.setText("");
    }
    
    public String getDateLabel() {
    	return date.getText();
    }
    
    public void setDateLabel() {
    	timeline.setCycleCount(Animation.INDEFINITE);
    	timeline.play();
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }
    
}


