package view;

import controller.Logic;
import model.TaskList;
import model.Task;





import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import exception.TaskInvalidDateException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainViewController extends GridPane{
	
	final static String[] colors = {
		"rgb(249, 233, 89)",
		"rgb(239, 75, 133)",
		"rgb(162, 100, 223)",
		"rgb(89, 225, 113)",
		"rgb(47, 217, 247)",
		"rgb(234, 106, 74)",
		"rgb(94, 236, 225)",
		"rgb(163, 220, 191)",
		"rgb(187, 239, 78)",
		"rgb(156, 184, 197)"
	};
	
	@FXML
	private Label uClear;
	
	@FXML
	private Label date;
	
	@FXML
	private Label response;
	
	@FXML
	private TextField input;
	
	@FXML
	private GridPane wholePane;
	
	@FXML
	private GridPane title;
	
	@FXML
	private Pane mainDisplay;
	
	@FXML
	private Pagination listDisplay;
	
	// Page information
	private VBox[] page;
	private ArrayList<ArrayList<GridPane>> content; 
	
	String command;
	String feedback;
	int pageCount;
	TaskList taskList;
	
	private Hashtable<String, String> tagColor;
	private int colorPointer;
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd  HH : mm : ss");
	final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			final Calendar cal = Calendar.getInstance();
			date.setText(dateFormat.format(cal.getTime()));
		}
	}));

	public MainViewController() throws IOException, TaskInvalidDateException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        fxmlLoader.load();
        
        initialize();
	}
	
	private void initialize() throws TaskInvalidDateException {
		setPageCount(5);
		setPages();
		setFont();
		setTagColor();
        setDate();
        initMainDisplay();
        setResponse();
	}
	
	private void setTagColor() {
		tagColor = new Hashtable<String, String>();
		colorPointer = 0;
	}
	
	private void setPageCount(int count) {
		pageCount = count;
	}

	private void setPages() {
		page = new VBox[pageCount];
		content = new ArrayList<ArrayList<GridPane>>(pageCount);
		for (int i=0; i<pageCount; i++) {
			page[i] = new VBox();
			page[i].setPrefHeight(listDisplay.getPrefHeight());
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			//content.set(i, new ArrayList<GridPane>());
		}
	}
	
	private void setFont() {
	    wholePane.setStyle("-fx-font-family: Montserrat-Regular");
	    uClear.setStyle("-fx-font-size: 35");
	    date.setStyle("-fx-font-size: 15");
	    response.setStyle("-fx-font-size: 20");
	    input.setStyle("-fx-font-size: 20");
	}

	private void setDate() {
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	private void setResponse() {
		if (taskList.count() > 1) {
			response.setText("Oops! " + taskList.count() + " tasks should be done!");
		} else if (taskList.count() == 1) {
			response.setText("Oops! " + taskList.count() + " task should be done!");
		} else {
			response.setText("Good! All tasks are done!");
		}
	}

	private void initMainDisplay() throws TaskInvalidDateException {
		listDisplay.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
		listDisplay.setPageCount(pageCount);
		listDisplay.setPageFactory(new Callback<Integer, Node>() 
		{
			@Override
			public Node call(Integer pageIndex) {
				return page[pageIndex];
			}
		});
		displayExistingTasks();
	}
	
	private void displayExistingTasks() throws TaskInvalidDateException {
		Logic.loadTaskList();
		taskList = Logic.getTaskList();
		
		for (int i=0; i<taskList.count(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setPrefSize(383, 100);
			Task task = taskList.getTask(i);
			Label description = new Label((i+1) +". " + task.getDescription());
			description.setStyle("-fx-text-fill: rgb(175,225,252)");
			Label date = new Label("Deadline: ");
			date.setStyle("-fx-text-fill: rgb(249,192,162)");
			
			Label status = new Label(task.displayDone());
			status.setStyle("-fx-text-fill: yellow");
			taskLayout.setConstraints(status, 0, 2, 10, 1);
			taskLayout.getChildren().add(status);
			
			if (task.getTags().size() > 0) {
				Label[] tags = new Label[task.getTags().size()];
				
				for (int j=0; j<task.getTags().size(); j++) {
					List<String> tagList = task.getTags();
					tags[j] = new Label(tagList.get(j));
					Label space = new Label("  ");
					
					if (!tagColor.containsKey(tags[j].getText())) {
						tagColor.put(tags[j].getText(), colors[colorPointer]);
						colorPointer = (colorPointer + 1) % 10;
					}
					tags[j].setStyle("-fx-background-color: " + tagColor.get(tags[j].getText()) + "; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					
//					tags[j].setStyle("-fx-background-color: skyblue; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					space.setStyle("-fx-background-color: rgb(127,127,127)");
					taskLayout.setConstraints(tags[j], 2*j, 3);
					taskLayout.setConstraints(space, 2*j+1, 3);
					taskLayout.getChildren().addAll(space, tags[j]);
				}
				
			} else {
				Label[] tags = new Label[1];
				tags[0] = new Label("None");
				tags[0].setStyle("-fx-background-color: black; -fx-text-fill: white");
				taskLayout.setConstraints(tags[0], 0, 3);
				taskLayout.getChildren().add(tags[0]);
			}
			
			taskLayout.setConstraints(description, 0, 0, 10, 1);
			taskLayout.setConstraints(date, 0, 1, 10, 1);
			taskLayout.getChildren().addAll(description, date);
			
			page[0].getChildren().add(taskLayout);
		}
	}
	
	private void updateDisplay() {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		taskList = Logic.getTaskList();
		
		for (int i=0; i<taskList.count(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setPrefSize(383, 100);
			Task task = taskList.getTask(i);
			Label description = new Label((i+1) +". " + task.getDescription());
			description.setStyle("-fx-text-fill: rgb(175,225,252)");
			Label date = new Label("Deadline: ");
			date.setStyle("-fx-text-fill: rgb(249,192,162)");
			
			Label status = new Label(task.displayDone());
			status.setStyle("-fx-text-fill: yellow");
			taskLayout.setConstraints(status, 0, 2, 10, 1);
			taskLayout.getChildren().add(status);
			
			if (task.getTags().size() > 0) {
				Label[] tags = new Label[task.getTags().size()];
				
				for (int j=0; j<task.getTags().size(); j++) {
					List<String> tagList = task.getTags();
					tags[j] = new Label(tagList.get(j));
					Label space = new Label("  ");
					
					if (!tagColor.containsKey(tags[j].getText())) {
						tagColor.put(tags[j].getText(), colors[colorPointer]);
						colorPointer = (colorPointer + 1) % 10;
					}
					tags[j].setStyle("-fx-background-color: " + tagColor.get(tags[j].getText()) + "; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					
//					tags[j].setStyle("-fx-background-color: skyblue; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					space.setStyle("-fx-background-color: rgb(127,127,127)");
					taskLayout.setConstraints(tags[j], 2*j, 3);
					taskLayout.setConstraints(space, 2*j+1, 3);
					taskLayout.getChildren().addAll(space, tags[j]);
				}
				
			} else {
				Label[] tags = new Label[1];
				tags[0] = new Label("None");
				tags[0].setStyle("-fx-background-color: black; -fx-text-fill: white");
				taskLayout.setConstraints(tags[0], 0, 3);
				taskLayout.getChildren().add(tags[0]);
			}
			
			taskLayout.setConstraints(description, 0, 0, 10, 1);
			taskLayout.setConstraints(date, 0, 1, 10, 1);
			taskLayout.getChildren().addAll(description, date);
			
			page[currentPageNum].getChildren().add(taskLayout);
		}
	}
	
	private void updateDisplay(int pageIndex) {

		taskList = Logic.getTaskList();
		
		for (int i=0; i<taskList.count(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setPrefSize(383, 100);
			Task task = taskList.getTask(i);
			Label description = new Label((i+1) +". " + task.getDescription());
			description.setStyle("-fx-text-fill: rgb(175,225,252)");
			Label date = new Label("Deadline: ");
			date.setStyle("-fx-text-fill: rgb(249,192,162)");
			
			Label status = new Label(task.displayDone());
			status.setStyle("-fx-text-fill: yellow");
			taskLayout.setConstraints(status, 0, 2, 10, 1);
			taskLayout.getChildren().add(status);
			
			if (task.getTags().size() > 0) {
				Label[] tags = new Label[task.getTags().size()];
				
				for (int j=0; j<task.getTags().size(); j++) {
					List<String> tagList = task.getTags();
					tags[j] = new Label(tagList.get(j));
					Label space = new Label("  ");
					
					if (!tagColor.containsKey(tags[j].getText())) {
						tagColor.put(tags[j].getText(), colors[colorPointer]);
						colorPointer = (colorPointer + 1) % 10;
					}
					tags[j].setStyle("-fx-background-color: " + tagColor.get(tags[j].getText()) + "; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					
//					tags[j].setStyle("-fx-background-color: skyblue; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
					space.setStyle("-fx-background-color: rgb(127,127,127)");
					taskLayout.setConstraints(tags[j], 2*j, 3);
					taskLayout.setConstraints(space, 2*j+1, 3);
					taskLayout.getChildren().addAll(space, tags[j]);
				}
				
			} else {
				Label[] tags = new Label[1];
				tags[0] = new Label("None");
				tags[0].setStyle("-fx-background-color: black; -fx-text-fill: white");
				taskLayout.setConstraints(tags[0], 0, 3);
				taskLayout.getChildren().add(tags[0]);
			}
			
			taskLayout.setConstraints(description, 0, 0, 10, 1);
			taskLayout.setConstraints(date, 0, 1, 10, 1);
			taskLayout.getChildren().addAll(description, date);
			
			page[pageIndex].getChildren().add(taskLayout);
		}
	}

	private void changePage(int pageNum) {
		listDisplay.setCurrentPageIndex(pageNum % pageCount);
	}
	
	private void closePage() {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		
		page[currentPageNum].getChildren().clear();
	}
	
	private void closePage(int pageIndex) {
		page[pageIndex].getChildren().clear();
	}
	
	private void setMainDisplay() throws TaskInvalidDateException {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		closePage(currentPageNum);
		setResponse();
		updateDisplay();
	}
	
	private String getUserInput() {
		return input.getText();
	}
	
	private String executeCommand(String command) {
		return Logic.readAndExecuteCommands(command);
	}
	
	@FXML
    private void onEnter() throws TaskInvalidDateException {
		command = getUserInput();
		
		if (command.trim().toLowerCase().equals("close")) {
			closePage();
		}
		
		if (command.trim().toLowerCase().equals("exit")) {
			Logic.saveTaskList();
			Platform.exit();
		}
		
		feedback = executeCommand(command);
		Logic.saveTaskList();
		setMainDisplay();
    	input.setText("");
    }
	
}


