package view;

import controller.Logic;
import controller.UserInput.CMD;
import model.TaskList;
import model.Task;
import exception.TaskInvalidDateException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
	
	final static KeyCode[] F_KEYS = {
		KeyCode.F1,
		KeyCode.F2,
		KeyCode.F3,
		KeyCode.F4,
		KeyCode.F5,
		KeyCode.F6,
		KeyCode.F7,
		KeyCode.F8,
	};
	
	final static CMD[] F_KEY_COMMAND = {
		CMD.ADD,
		CMD.DELETE,
		CMD.DONE,
		CMD.EDIT,
		CMD.SEARCH,
		CMD.SHOW,
		CMD.TAG,
	};
	
	final static String CSS_BACKGROUND_COLOR = "-fx-background-color: ";
	final static String FX_COLOR_RGB = "rgb(%s, %s, %s)";
	final static String FXML_FILE_NAME = "MainView.fxml";
	final static String ONE_TASK_NOT_DONE = "Oops! 1 task should be done!";
	final static String MANY_TASKS_NOT_DONE = "Oops! %s tasks should be done!";
	final static String ALL_TASKS_DONE = "Good! All tasks are done!";
	
	final static String TITLE_ALL_TASKS = "All Tasks";
	final static String TITLE_DONE_TASKS = "Done Tasks";
	final static String TITLE_SEARCH_RESULT = "Search Result";
	final static String TITLE_HELP_PAGE = "Help Document";
	
	final static String MESSAGE_HELP = "Current list of available commands: \n"
			+ "- add a floating task     : add <description>\n"
			+ "- add a deadline task     : add <description> by <time/date>\n"
			+ "- add a fixed time        : add <description> <time/date1> to <time/date2>\n"
			+ "- add a repeated task     : add <description> every <time/date> <period(daily/weekly/monthly)>\n"
			+ "- edit a task description : edit <taskID> <description>\n"
			+ "- edit a task time/date   : edit <taskID> <time/date>\n"
			+ "- delete task(s)          : delete <taskID> [<taskID> <taskID> ...]\n"
			+ "- clear all tasks         : clear\n"
			+ "- mark task(s) done       : done <taskID> [<taskID> <taskID> ...]\n"
			+ "- tag a task              : tag <taskID> <tag>\n"
			+ "- untag a task            : untag <taskID> <tag>\n"
			+ "- untag all tags from task: untag <taskID>\n"
			+ "- show all tasks          : show / show all\n"
			+ "- show tasks (add order)  : show added\n"
			+ "- show tasks with tag     : show <tag>\n"
			+ "- show tasks that are done: show done\n"
			+ "(You can only delete or search tasks when displying tasks that are done)\n"
			+ "- search tasks            : search <keyword>\n"
			+ "- exit the program        : exit";
	
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
	
	String searchKey;
	
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
        setDisplayTitleText();
	}
	
	public ScrollPane[] getScrollPages() {
		return scrollPage;
	}
	
	private void setDisplayTitleText() {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		
		if (currentPageNum == 0) {
			displayTitleText.setText(TITLE_ALL_TASKS);
		} else if (currentPageNum == 1) {
			displayTitleText.setText(TITLE_DONE_TASKS);
		} else if (currentPageNum == 2) {
			displayTitleText.setText(TITLE_SEARCH_RESULT);
		} else {
			displayTitleText.setText(TITLE_HELP_PAGE);
		}
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
			setScrollPage(i);
			page[i] = new VBox();
			page[i].setPrefHeight(listDisplay.getPrefHeight());
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			scrollPage[i].setContent(page[i]);
		}
		
		setLeftKey();
		setRightKey();
		for (int i=0; i<8; i++) {
			setFKey(i);
		}
		
	}
	
	private void setScrollPage(int index) {
		scrollPage[index].setStyle(CSS_BACKGROUND_COLOR + String.format(FX_COLOR_RGB, 127, 127, 127));
		scrollPage[index].setPrefSize(listDisplay.getPrefWidth(), listDisplay.getPrefHeight());
		scrollPage[index].setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPage[index].setHbarPolicy(ScrollBarPolicy.NEVER);
	}
	
	private void setLeftKey() {
		listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
		     @Override 
		     public void handle(KeyEvent event ) {
		        if (event.getCode() == KeyCode.LEFT) {
//		        	event.consume();
		        	boolean flag = true;
		        	if (flag) {
		        		flag = false;
		        		if (listDisplay.getCurrentPageIndex() == 0) {
		    				try {
								displayShowAllCommand();
							} catch (TaskInvalidDateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			} else if (listDisplay.getCurrentPageIndex() == 1) {
		    				try {
								displayShowDoneCommand();
							} catch (TaskInvalidDateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			} else if (listDisplay.getCurrentPageIndex() == 2) {
		    				try {
								displaySearchCommand(searchKey);
							} catch (TaskInvalidDateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			}
		        	}
		        }
		     }
		});
	}
	
	private void setRightKey() {
		listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
		     @Override 
		     public void handle(KeyEvent event ) {
		        if (event.getCode() == KeyCode.RIGHT) {
//		        	event.consume();
		        	boolean flag = true;
		        	if (flag) {
		        		flag = false;
		        		if (listDisplay.getCurrentPageIndex() == 3) {
		    				displayHelpCommand();
		    			} else if (listDisplay.getCurrentPageIndex() == 1) {
		    				try {
								displayShowDoneCommand();
							} catch (TaskInvalidDateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			} else if (listDisplay.getCurrentPageIndex() == 2) {
		    				try {
								displaySearchCommand(searchKey);
							} catch (TaskInvalidDateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			}
		        	}
		        }
		     }
		});
	}
	
	private void setFKey(final int index) {
		listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
		     @Override 
		     public void handle(KeyEvent event ) {
		        if (event.getCode() == F_KEYS[index]) {
//		        	event.consume();
		        	boolean flag = true;
		        	if (flag) {
		        		flag = false;
		        		if (listDisplay.getCurrentPageIndex() == 3) {
		    				if (index != 7) {
		    					setHelpPage(F_KEY_COMMAND[index]);
		    				} else {
		    					setHelpPage();
		    				}
		    			}
		        	}
		        }
		     }
		});
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
	
	private void setHelpPage() {
		
	}

	private void setHelpPage(CMD commandType) {
		
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
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<taskList.countUndone(); i++) {
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
	
	private void setTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		
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
	
	private void clearPage(int pageIndex) {
		page[pageIndex].getChildren().clear();
	}
	
	private void setMainDisplay() throws TaskInvalidDateException {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		clearPage(currentPageNum);
		updateDisplay();
	}
	
	private void setRestTaskResponse() {
		if (listDisplay.getCurrentPageIndex() == 0) {
			if (taskList.countUndone() > 1) {
				response.setText(String.format(MANY_TASKS_NOT_DONE, taskList.countUndone()));
			} else if (taskList.countUndone() == 1) {
				response.setText(ONE_TASK_NOT_DONE);
			} else {
				response.setText(ALL_TASKS_DONE);
			}
		} else if (listDisplay.getCurrentPageIndex() == 1) {
			if (taskList.countFinished() == 0) {
				response.setText("You haven't finished any tasks yet!");
			} else if (taskList.countFinished() == 1) {
				response.setText("Good! 1 task has been finished!");
			} else {
				response.setText("Good! " + taskList.countFinished() + " tasks have been finished!");
			}
		} else if (listDisplay.getCurrentPageIndex() == 2) {
			if (searchKey == null) {
				response.setText("No Result.");
			} else {
				int count = taskList.searchTaskByKeyword(searchKey).size();
				if (count == 0) {
					response.setText("No Result.");
				} else if (count == 1) {
					response.setText("1 result shown.");
				} else {
					response.setText(count + "results shown.");
				}
			}
		} else {
			response.setText("Valid commands shown.");
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
	
	private String getHelpInfo() {
		return MESSAGE_HELP;
	}
	
	private void setTextField(String content) {
		input.setText(content);
	}
	
	private void setTextFieldEmpty() {
		setTextField("");
	}
	
	private void displayHelpCommand() {
		listDisplay.setCurrentPageIndex(3);
		
		page[3].getChildren().clear();
		
		Text helpDoc = new Text(getHelpInfo());
		helpDoc.setStyle("-fx-text-fill: yellow");
		page[3].getChildren().add(helpDoc);
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displaySearchCommand(String searchKey) throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(2);
		
		page[2].getChildren().clear();
		
		if (searchKey != null) {
			ArrayList<Task> searchTaskList = (ArrayList<Task>)taskList.searchTaskByKeyword(searchKey);
			for (int i=0; i<searchTaskList.size(); i++) {
				
				Task task = searchTaskList.get(i);
				GridPane taskLayout = new GridPane();
				taskLayout.setStyle("-fx-padding: 15; -fx-font-size: 15");
				
				setGridPaneSize(taskLayout, 383, 100);
				
				setTaskFormat(taskLayout, task, i);
				
				page[2].getChildren().add(taskLayout);
			}
		}
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowAllCommand() throws TaskInvalidDateException {
		taskList.setNotShowingDone();
		listDisplay.setCurrentPageIndex(0);
		setDisplayTitleText();
		setRestTaskResponse();
		setMainDisplay();
	}
	
	private void displayShowDoneCommand() throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(1);
		page[1].getChildren().clear();
		
		ArrayList<Task> doneTaskList = (ArrayList<Task>)taskList.getFinishedTasks();
		for (int i=0; i<doneTaskList.size(); i++) {
			
			Task task = doneTaskList.get(i);
			GridPane taskLayout = new GridPane();
			taskLayout.setStyle("-fx-padding: 15; -fx-font-size: 15");
			
			setGridPaneSize(taskLayout, 383, 100);
			
			setTaskFormat(taskLayout, task, i);
			
			page[1].getChildren().add(taskLayout);
		}
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayOtherCommand() throws TaskInvalidDateException {
		if (listDisplay.getCurrentPageIndex() == 0) {
			setMainDisplay();
		} else if (listDisplay.getCurrentPageIndex() == 1) {
			page[1].getChildren().clear();
			
			ArrayList<Task> doneTaskList = (ArrayList<Task>)taskList.getFinishedTasks();
			for (int i=0; i<doneTaskList.size(); i++) {
				
				Task task = doneTaskList.get(i);
				GridPane taskLayout = new GridPane();
				taskLayout.setStyle("-fx-padding: 15; -fx-font-size: 15");
				
				setGridPaneSize(taskLayout, 383, 100);
				
				setTaskFormat(taskLayout, task, i);
				
				page[1].getChildren().add(taskLayout);
			}
		} else if (listDisplay.getCurrentPageIndex() == 2) {
			page[2].getChildren().clear();
			
			ArrayList<Task> searchTaskList = (ArrayList<Task>)taskList.searchTaskByKeyword(searchKey);
			for (int i=0; i<searchTaskList.size(); i++) {
				
				Task task = searchTaskList.get(i);
				GridPane taskLayout = new GridPane();
				taskLayout.setStyle("-fx-padding: 15; -fx-font-size: 15");
				
				setGridPaneSize(taskLayout, 383, 100);
				
				setTaskFormat(taskLayout, task, i);
				
				page[2].getChildren().add(taskLayout);
			}
		}
		response.setText(feedback);
		
		if (feedback.length() > 10) {
			response.setStyle("-fx-font-size: 15;-fx-text-fill: rgb(68,217,117)");
		} else {
			response.setStyle("-fx-text-fill: rgb(68,217,117)");
		}
		
		fadeOut.playFromStart();
		setDisplayTitleText();
	}
	
	private void analyseCommand(String command) throws TaskInvalidDateException {
		if (command.trim().length() == 4 && command.trim().toLowerCase().substring(0, 4).equals("help")) {
			displayHelpCommand();
		} else if (command.trim().length() > 6 && command.trim().toLowerCase().substring(0, 6).equals("search")) {
			searchKey = command.trim().substring(7);
			displaySearchCommand(searchKey);
		} else if ((command.trim().length() == 8 && command.trim().toLowerCase().substring(0, 8).equals("show all")) 
				|| (command.trim().length() == 4 && command.trim().toLowerCase().substring(0, 4).equals("show"))) {
			displayShowAllCommand();
		} else if (command.trim().length() == 9 && command.trim().toLowerCase().substring(0, 9).equals("show done")) {
			displayShowDoneCommand();
		// except show, search and help
		} else {
			displayOtherCommand();
		}
	}
	
	@FXML
    private void onEnter() throws TaskInvalidDateException {
		command = getUserInput();
		
		if (!command.equals("")) {			
			
			if (!isSpecialCommand()) {
				feedback = executeCommand(command);
				taskList = getTaskList();
				if (listDisplay.getCurrentPageIndex() != 1) {
					taskList.setNotShowingDone();
				}
				setTextFieldEmpty();
				saveTaskList();
				
				analyseCommand(command);
				
				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						setRestTaskResponse();
						response.setStyle("-fx-font-size: 20;-fx-text-fill: rgb(241,109,82)");
					}
					
				});
			}
		}
    }
	
	@FXML
	private void onKeyTyped(KeyEvent keyEvent) throws TaskInvalidDateException {
		if (keyEvent.getCharacter().equals("1")) {
			displayShowAllCommand();
		}
		if (keyEvent.getCharacter().equals("2")) {
			displayShowDoneCommand();
		}
		if (keyEvent.getCharacter().equals("3")) {
			displaySearchCommand(searchKey);
		}
		if (keyEvent.getCharacter().equals("4")) {
			displayHelpCommand();
		}
		if (keyEvent.getCharacter().equals("a")) {
			setTextField("add ");
			input.requestFocus();
			input.selectEnd();
		}
		if (keyEvent.getCharacter().equals("d")) {
			setTextField("done ");
			input.requestFocus();
			input.selectEnd();
		}
		if (keyEvent.getCharacter().equals("e")) {
			setTextField("edit ");
			input.requestFocus();
			input.selectEnd();
		}
		if (keyEvent.getCharacter().equals("t")) {
			setTextField("tag ");
			input.requestFocus();
			input.selectEnd();
		}
		if (keyEvent.getCharacter().equals("h")) {
			setTextField("help");
			input.requestFocus();
			input.selectEnd();
		}
	}
	
}


