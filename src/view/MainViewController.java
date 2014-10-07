package view;

import controller.Logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import model.TaskList;
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
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the GUI
 * 
 * @author Wang Zhipeng
 *
 */

public class MainViewController extends VBox {
	
	private static final String MESSAGE_NO_TASK = "Good! All tasks are clear!";
	private static final String MESSAGE_TASKS_EXIST = "Oops! %s to be completed!";
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			final Calendar cal = Calendar.getInstance();
			date.setText(dateFormat.format(cal.getTime()));
		}
	}));
	
    @FXML 
    private TextField textField;
    
    @FXML
    private Label date;
    
    @FXML
    private VBox toBeCompleted;
    
    @FXML
    private VBox toDo;
    
    @FXML
    private Label popMessage;
    
	Label displayTaskList = new Label();
	TaskList taskList;
	int countTasks;
	String tasks;
    
//    private ArrayList<String> toDoTaskList;
//    
//    private ArrayList<String> toBeCompletedTaskList;

    public MainViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
//        toDoTaskList = new ArrayList<String>();
//        toBeCompletedTaskList = new ArrayList<String>();
        
        Logic.initialize();
        toBeCompleted.getChildren().add(displayTaskList);
        
        setTaskListView();
		determinePopMessage();
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

//    	Label displayForToBeCompleted = new Label();
//    	Label displayForToDo = new Label();
    	
    	String input = getInput();
    	
    	if (input.equals("exit")) {
    		Stage stage = (Stage) getScene().getWindow();
    		stage.close();
    	} else {
    	
    		String feedback = Logic.readAndExecuteCommands(input);
    	
    		setTaskListView();
    		determinePopMessage();
    		Logic.saveTaskList();
    	}
    	
//    	displayForToBeCompleted.setText(feedback);
//    	displayForToDo.setText(Logic.getTaskList().getList().toString());
//    	toBeCompleted.getChildren().add(displayForToBeCompleted);
//    	toDo.getChildren().add(displayForToDo);
    }
    
    private void setTaskListView() {
    	taskList = Logic.getTaskList();
		countTasks = taskList.getNumberOfTasks();
		tasks = taskList.toString();
	
		displayTaskList.setText(tasks);
    }
    
    private void determinePopMessage() {
		if(tasks.equals("")) {
			popMessage.setText(MESSAGE_NO_TASK);
		} else {
			popMessage.setText(String.format(MESSAGE_TASKS_EXIST, countTasks));
		}
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


