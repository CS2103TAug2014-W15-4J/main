package view;

import controller.Logic;
import model.TaskList;
import model.Task;
import exception.TaskInvalidDateException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainViewController extends GridPane{
	
	final static String[] DEFAULT_COLORS = {
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
	
	final static String CSS_BACKGROUND_COLOR = "-fx-background-color: ";
	final static String FX_COLOR_RGB = "rgb(%s, %s, %s)";
	final static String FXML_FILE_NAME = "MainView.fxml";
	final static String ONE_TASK_NOT_DONE = "Oops! 1 task should be done!";
	final static String MANY_TASKS_NOT_DONE = "Oops! %s tasks should be done!";
	final static String ALL_TASKS_DONE = "Good! All tasks are done!";
	
	@FXML
	private Label uClear;
	
	@FXML
	private Label date;
	
	@FXML
	private Label response;
	
	@FXML
	private Label displayTitleText;
	
	@FXML
	private TextField input;
	
	@FXML
	private GridPane wholePane;
	
	@FXML
	private GridPane title;
	
	@FXML
	private Pagination listDisplay;
	
	private FadeTransition fadeOut = new FadeTransition(Duration.millis(200));
	
	// Pages
	private ScrollPane[] scrollPage;
	private VBox[] page;
	
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
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        fxmlLoader.load();
        
        setMainView();
	}
	
	private void setMainView() throws TaskInvalidDateException {
		initFadeEffect();
		setPageCount(4);
		setPages();
		setFont();
		initTagColor();
        setDate();
        initMainDisplay();
        setRestTaskResponse();
	}
	
	private void initFadeEffect() {
		fadeOut.setNode(response);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.65);
		fadeOut.setCycleCount(10);
		fadeOut.setAutoReverse(true);
	}

	private void setPageCount(int count) {
		pageCount = count;
	}

	private void setPages() {
		scrollPage = new ScrollPane[pageCount];
		page = new VBox[pageCount];
		for (int i=0; i<pageCount; i++) {
			scrollPage[i] = new ScrollPane();
			setScrollPage(scrollPage[i]);
			page[i] = new VBox();
			page[i].setPrefHeight(listDisplay.getPrefHeight());
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			scrollPage[i].setContent(page[i]);
		}
	}
	
	private void setScrollPage(ScrollPane scroll) {
		scroll.setStyle(CSS_BACKGROUND_COLOR + String.format(FX_COLOR_RGB, 127, 127, 127));
		scroll.setPrefSize(listDisplay.getPrefWidth(), listDisplay.getPrefHeight());
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
	}

	private void setFont() {
	    wholePane.setStyle("-fx-font-family: Montserrat-Regular");
	    uClear.setStyle("-fx-font-size: 35");
	    date.setStyle("-fx-font-size: 15");
	    response.setStyle("-fx-font-size: 20");
	    input.setStyle("-fx-font-size: 20");
	    displayTitleText.setStyle("-fx-font-size: 25");
	}

	private void initTagColor() {
		tagColor = new Hashtable<String, String>();
		colorPointer = 0;
	}
	
	private void setDate() {
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	private void initMainDisplay() throws TaskInvalidDateException {
		listDisplay.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
		listDisplay.setPageCount(pageCount);
		listDisplay.setPageFactory(new Callback<Integer, Node>() 
		{
			@Override
			public Node call(Integer pageIndex) {
				return scrollPage[pageIndex];
			}
		});
		displayExistingTasks();
	}

	private void displayExistingTasks() throws TaskInvalidDateException {
		loadTaskList();
		taskList = getTaskList();
		
		setOnePageView(0);
	}
	
	private void updateDisplay() throws TaskInvalidDateException {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		taskList = getTaskList();
		
		setOnePageView(currentPageNum);
	}
	
	private void setOnePageView(int pageIndex) throws TaskInvalidDateException {
		for (int i=0; i<taskList.count(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setStyle("-fx-padding: 15; -fx-font-size: 15");
			
			setGridPaneSize(taskLayout, 383, 100);
			
			setTaskFormat(taskLayout, i);
			
			page[pageIndex].getChildren().add(taskLayout);
		}
	}
	
	private void setTaskFormat(GridPane taskLayout, int index) throws TaskInvalidDateException {
		Task task = taskList.getTask(index);
		
		setDescription(taskLayout, task, index);
		
		setDeadline(taskLayout, task);
		
		setStatus(taskLayout, task);
		
		setTags(taskLayout, task);	
	}
	
	private void setDescription(GridPane taskLayout, Task task, int index) {
		Label description = new Label((index+1) +". " + task.getDescription());
		description.setStyle("-fx-text-fill: rgb(175,225,252)");
		GridPane.setConstraints(description, 0, 0, 10, 1);
		taskLayout.getChildren().add(description);
	}
	
	private void setDeadline(GridPane taskLayout, Task task) throws TaskInvalidDateException {
		Label deadline = new Label();
		
		if (task.getType().equals(Task.Type.FLOAT)) {
			deadline.setText("Deadline: No deadline");
		} else {
			deadline.setText("Deadline: " + dateFormat.format(task.getDeadline()));
		}
		
		deadline.setStyle("-fx-text-fill: rgb(249,192,162)");
		GridPane.setConstraints(deadline, 0, 1, 10, 1);
		taskLayout.getChildren().add(deadline);
	}
	
	private void setStatus(GridPane taskLayout, Task task) {
		Label status = new Label(task.displayDone());
		status.setStyle("-fx-text-fill: yellow");
		GridPane.setConstraints(status, 0, 2, 10, 1);
		taskLayout.getChildren().add(status);
	}
	
	private void setTags(GridPane taskLayout, Task task) {
		if (task.getTags().size() > 0) {
			Label[] tags = new Label[task.getTags().size()];
			
			for (int j=0; j<task.getTags().size(); j++) {
				List<String> tagList = task.getTags();
				tags[j] = new Label(tagList.get(j));
				Label space = new Label("  ");
				
				if (!tagColor.containsKey(tags[j].getText())) {
					tagColor.put(tags[j].getText(), DEFAULT_COLORS[colorPointer]);
					colorPointer = (colorPointer + 1) % 10;
				}
				tags[j].setStyle(CSS_BACKGROUND_COLOR + tagColor.get(tags[j].getText()) + "; -fx-text-fill: white; -fx-label-padding: 1 2 1 2;");
				
				space.setStyle(CSS_BACKGROUND_COLOR + String.format(FX_COLOR_RGB, 127, 127, 127));
				GridPane.setConstraints(tags[j], 2*j, 3);
				GridPane.setConstraints(space, 2*j+1, 3);
				taskLayout.getChildren().addAll(space, tags[j]);
			}
			
		} else {
			Label[] tags = new Label[1];
			tags[0] = new Label("None");
			tags[0].setStyle("-fx-background-color: black; -fx-text-fill: white");
			GridPane.setConstraints(tags[0], 0, 3);
			taskLayout.getChildren().add(tags[0]);
		}
	}
	
	private void setGridPaneSize(GridPane gridPane, double width, double height) {
		gridPane.setPrefSize(width, height);
		gridPane.setMaxSize(width, height);
		gridPane.setMinSize(width, height);
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
		updateDisplay();
	}
	
	private void setRestTaskResponse() {
		if (taskList.count() > 1) {
			response.setText(String.format(MANY_TASKS_NOT_DONE, taskList.count()));
		} else if (taskList.count() == 1) {
			response.setText(ONE_TASK_NOT_DONE);
		} else {
			response.setText(ALL_TASKS_DONE);
		}
	}

	private String getUserInput() {
		return input.getText();
	}
	
	private String executeCommand(String command) {
		return Logic.readAndExecuteCommands(command);
	}
	
	private boolean isSpecialCommand() throws TaskInvalidDateException {
		if (command.trim().toLowerCase().equals("close")) {
			closePage();
			setTextFieldEmpty();
			return true;
		}
		
		if (command.trim().toLowerCase().equals("exit")) {
			saveTaskList();
			Platform.exit();
		}
		
		return false;
	}
	
	private void saveTaskList() {
		Logic.saveTaskList();
	}
	
	private void loadTaskList() {
		Logic.loadTaskList();
	}
	
	private TaskList getTaskList() {
		return Logic.getTaskList();
	}
	
	private void setTextFieldEmpty() {
		input.setText("");
	}
	
	@FXML
    private void onEnter() throws TaskInvalidDateException {
		command = getUserInput();
		
		if (!command.equals("")) {			
			
			if (!isSpecialCommand()) {
				feedback = executeCommand(command);
				setTextFieldEmpty();
				saveTaskList();
				
				if (command.length() > 3 && command.trim().toLowerCase().substring(0, 3).equals("show")) {
					System.out.println("haha");
					feedback = response.getText();
				}
				
				setMainDisplay();
				response.setText(feedback);
				response.setStyle("-fx-text-fill: rgb(68,217,117)");
				fadeOut.playFromStart();
				
				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						setRestTaskResponse();
						response.setStyle("-fx-text-fill: rgb(241,109,82)");
					}
					
				});
			}
		}
    }
	
}


