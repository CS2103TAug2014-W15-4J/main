package view;

import controller.Logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import model.TaskList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the GUI
 * 
 * @author Wang Zhipeng
 *
 */

public class MainViewController extends VBox {
	
	private static final String MESSAGE_NO_TASK = "Good! All tasks are clear!";
	private static final String MESSAGE_TASKS_EXIST = "Oops! %s to be completed!";
	
	final static Logger logForMainViewController = Logger.getLogger(MainViewController.class.getName());
	
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

    public MainViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
        	logForMainViewController.log(Level.SEVERE, "Time out! Run time exception!");
            throw new RuntimeException(exception);
        }
        
        logForMainViewController.log(Level.INFO, "Load MainView.fxml successfully!");
        
        Logic.initialize();
        
        logForMainViewController.log(Level.INFO, "Initialize successfully!");
        
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
    	
    	String input = getInput();
    	
    	if (input.trim().toLowerCase().equals("exit")) {
    		Stage stage = (Stage) getScene().getWindow();
    		stage.close();
    	
    	} else if (input.trim().toLowerCase().equals("show")) {
    		
    		displayTaskList.setText(Logic.getDisplayInfo());
    		
    	} else {
    		
    		// Feedback from Logic
    		String feedback = Logic.readAndExecuteCommands(input);
    
    		logForMainViewController.log(Level.INFO, "Execute command complete!");    		
    		
    		fadeFeedback(feedback);
    		setTaskListView(feedback);
    		determinePopMessage();
    		
    		logForMainViewController.log(Level.INFO, "Set the view successfully!");
    		
    		Logic.saveTaskList();
    	}
    	
    }

	private void setTaskListView() {
    	taskList = Logic.getTaskList();
		countTasks = taskList.count();
		tasks = taskList.toString();
	
		displayTaskList.setText(tasks);
    }
	
	private void setTaskListView(String feedback) {
    	taskList = Logic.getTaskList();
		countTasks = taskList.count();
		tasks = taskList.toString();
		
		tasks = tasks + "\n" + feedback;
	
		displayTaskList.setText(tasks);
    }
    
    private void determinePopMessage() {
		if(countTasks == 0) {
			popMessage.setText(MESSAGE_NO_TASK);
			
			logForMainViewController.log(Level.INFO, "No existing task!");	
		} else {
			popMessage.setText(String.format(MESSAGE_TASKS_EXIST, countTasks));
			
			logForMainViewController.log(Level.INFO, "Load existing task!");
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
    
    public void fadeFeedback(String feedback) {
    	popMessage.setText(feedback);
    	
    	FadeTransition ft = new FadeTransition(Duration.millis(3000), popMessage);
    	ft.setFromValue(1.0);
    	ft.setToValue(0.1);
    	ft.setCycleCount(2);
    	ft.setAutoReverse(true);
    	ft.play();
    }
    
}


