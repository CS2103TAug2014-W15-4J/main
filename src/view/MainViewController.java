package view;

import controller.Logic;
import controller.UserInput.CMD;
import model.FixedTask;
import model.RepeatedTask;
import model.TaskList;
import model.Task;
import model.Task.Type;
import exception.TaskInvalidDateException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainViewController extends GridPane{
	
	final static String[] DEFAULT_TAG_COLORS = {
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
	
	final static String TITLE_TODAY_TASKS = "Today Tasks";
	final static String TITLE_ALL_TASKS = "All Tasks";
	final static String TITLE_DONE_TASKS = "Done Tasks";
	final static String TITLE_SEARCH_RESULT = "Search Result";
	final static String TITLE_HELP_PAGE = "Help Document";
	
	final static int TOTAL_PAGE_NUM = 5;
	final static int TODAY_TASKS_PAGE_INDEX = 0;
	final static int UNDONE_TASKS_PAGE_INDEX = 1;
	final static int DONE_TASKS_PAGE_INDEX = 2;
	final static int SEARCH_RESULT_PAGE_INDEX = 3;
	final static int HELP_DOC_PAGE_INDEX = 4;
	
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
	private GridPane[] commandInfo;
	private Label[] fKeys;
	private Label[] fKeysInfo;
	
	private boolean[] isCommandHelp;
	
	String command;
	String feedback;
	int pageCount;
	TaskList taskList;
	
	String searchKey;
	
	private Hashtable<String, String> tagColor;
	private int colorPointer;
	
	final SimpleDateFormat taskTimeFormat = new SimpleDateFormat("MMM dd, EE  HH : mm", Locale.US);
	final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd  HH : mm : ss", Locale.US);
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
		setIsCommandHelp();
		initFadeEffect();
		setPageCount(TOTAL_PAGE_NUM);
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
	
	private void setIsCommandHelp() {
		isCommandHelp = new boolean[8];
		
		for (int i=0; i<8; i++) {
			isCommandHelp[i] = false;
		}
	}
	
	private void setDisplayTitleText() {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		
		if (currentPageNum == TODAY_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_TODAY_TASKS);
		} else if (currentPageNum == UNDONE_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_ALL_TASKS);
		} else if (currentPageNum == DONE_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_DONE_TASKS);
		} else if (currentPageNum == SEARCH_RESULT_PAGE_INDEX) {
			displayTitleText.setText(TITLE_SEARCH_RESULT);
		} else if (currentPageNum == HELP_DOC_PAGE_INDEX){
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
			page[i].setPrefHeight(listDisplay.getPrefHeight()+66);
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			page[i].setStyle("-fx-background-color: white; -fx-padding: 20;");
			scrollPage[i].setContent(page[i]);
		}
		
		setLeftKey();
		setRightKey();
		setEscKey();
		for (int i=0; i<8; i++) {
			setFKey(i);
		}
		
	}
	
	private void setScrollPage(int index) {
		scrollPage[index].setStyle(CSS_BACKGROUND_COLOR + String.format(FX_COLOR_RGB, 255, 255, 255));
		scrollPage[index].setPrefSize(listDisplay.getPrefWidth(), listDisplay.getPrefHeight());
		scrollPage[index].setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPage[index].setHbarPolicy(ScrollBarPolicy.NEVER);
	}
	
	private void setFont() {
//	    wholePane.setStyle("-fx-font-family: Montserrat-Regular");
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
		loadTaskListToController();
		displayTodayTasks();
	}
	
	private void setHelpHomePage() throws TaskInvalidDateException {
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		setRestTaskResponse();
		
		commandInfo = new GridPane[8];
		fKeys = new Label[8];
		fKeysInfo = new Label[8];
		
		for (int i=0; i<7; i++) {
			
			commandInfo[i] = new GridPane();
			setHelpHBoxSize(i, 900, 40);
			
			fKeys[i] = new Label(F_KEYS[i].toString());
			fKeys[i].setStyle("-fx-padding: 0; -fx-font-size: 20; -fx-text-fill: skyblue");
			
			fKeysInfo[i] = new Label(F_KEY_COMMAND[i].toString());
			fKeysInfo[i].setStyle("-fx-padding: 0; -fx-font-size: 20; -fx-text-fill: lightgreen");
			
			GridPane.setConstraints(fKeys[i], 0, 0);
			commandInfo[i].getChildren().add(0, fKeys[i]);
			GridPane.setConstraints(fKeysInfo[i], 1, 0);
			commandInfo[i].getChildren().add(1, fKeysInfo[i]);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[i]);
			
			GridPane.setMargin(fKeys[i], new Insets(5, 50, 5, 15));
			GridPane.setMargin(fKeysInfo[i], new Insets(5, 15, 5, 15));
			
		}
		
		commandInfo[7] = new GridPane();
		setHelpHBoxSize(7, 900, 40);
		
		fKeys[7] = new Label(F_KEYS[7].toString());
		fKeys[7].setStyle("-fx-padding: 0; -fx-font-size: 20; -fx-text-fill: skyblue");
		
		fKeysInfo[7] = new Label("OTHER COMMANDS");
		fKeysInfo[7].setStyle("-fx-padding: 0; -fx-font-size: 20; -fx-text-fill: lightgreen");
		
		GridPane.setConstraints(fKeys[7], 0, 0);
		commandInfo[7].getChildren().add(fKeys[7]);
		GridPane.setConstraints(fKeysInfo[7], 1, 0);
		commandInfo[7].getChildren().add(fKeysInfo[7]);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[7]);
		
		GridPane.setMargin(fKeys[7], new Insets(5, 50, 5, 15));
		GridPane.setMargin(fKeysInfo[7], new Insets(5, 15, 5, 15));
	}
	
	private void setHelpHBoxSize(int index, double width, double height) {
		commandInfo[index].setPrefSize(width, height);
		commandInfo[index].setMaxSize(width, height);
		commandInfo[index].setMinSize(width, height);
	}
	
	private void setHelpForCommand(GridPane command, String descriptionString, String structureString, String exampleString) {
		Label description = new Label("Description");
		description.setStyle("-fx-text-fill: yellow");
		Label structure = new Label("Structure");
		structure.setStyle("-fx-text-fill: yellow");
		Label example = new Label("Example");
		example.setStyle("-fx-text-fill: yellow");
		
		command.setStyle("-fx-padding: 15; -fx-font-size: 14");
		setGridPaneSize(command, 900, 140);
		Label descriptionContent = new Label(descriptionString);
		descriptionContent.setStyle("-fx-text-fill: lightgreen");
		Label structureContent = new Label(structureString);
		structureContent.setStyle("-fx-text-fill: lightgreen");
		Label exampleContent = new Label(exampleString);
		exampleContent.setStyle("-fx-text-fill: lightgreen");
		
		GridPane.setConstraints(description, 0, 0);
		GridPane.setConstraints(descriptionContent, 0, 1);
		GridPane.setConstraints(structure, 0, 2);
		GridPane.setConstraints(structureContent, 0, 3);
		GridPane.setConstraints(example, 0, 4);
		GridPane.setConstraints(exampleContent, 0, 5);
		command.getChildren().addAll(description, descriptionContent, structure, structureContent, example, exampleContent);
	}

	private void setHelpPage(int index) {
		response.setText("Press \"Esc\" to return");
		
		if (index == 0) {
			
			Label add = new Label("ADD");
			add.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane addFloatTask = new GridPane();
			setHelpForCommand(addFloatTask, "      add a floating task", "      add <description>", "      add do homework");
			
			GridPane addDeadlineTask = new GridPane();
			setHelpForCommand(addDeadlineTask, "      add a deadline task", "      add <description> by <time/date>", "      add do homework by Oct 31");
			
			GridPane addRepeatedTask = new GridPane();
			setHelpForCommand(addRepeatedTask, "      add a repeated task", "      add <description> every <time/date> <period>", "      add do homework every week");
			
			GridPane addFixedTask = new GridPane();
			setHelpForCommand(addFixedTask, "      add a fixed task", "      add <description> <time/date1> to <time/date2>", "      add do homework Nov 10 to Nov 20");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(add, addFloatTask, addDeadlineTask, addRepeatedTask, addFixedTask);
			
		} else if (index == 1) {
			
			Label delete = new Label("DELETE");
			delete.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane deleteOneTask = new GridPane();
			setHelpForCommand(deleteOneTask, "      delete one task", "      delete <taskID>", "      delete 1");
			
			GridPane deleteManyTasks = new GridPane();
			setHelpForCommand(deleteManyTasks, "      delete many tasks", "      delete <taskID_1> <taskID_2> ...", "      delete 1 2 3");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(delete, deleteOneTask, deleteManyTasks);
			
		} else if (index == 2) {
			
			Label done = new Label("DONE");
			done.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane doneOneTask = new GridPane();
			setHelpForCommand(doneOneTask, "      mark one task done", "      done <taskID>", "      done 1");
			
			GridPane doneManyTasks = new GridPane();
			setHelpForCommand(doneManyTasks, "      mark many tasks done", "      done <taskID_1> <taskID_2> ...", "      done 1 2 3");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(done, doneOneTask, doneManyTasks);
			
		} else if (index == 3) {
			
			Label edit = new Label("EDIT");
			edit.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane editTaskDescription = new GridPane();
			setHelpForCommand(editTaskDescription, "      edit task description of one task", "      edit <taskID> <new description>", "      edit 1 sleep");
			
			GridPane editTaskTime = new GridPane();
			setHelpForCommand(editTaskTime, "      edit task time of one task", "      edit <taskID> <new time>", "      edit 1 Oct 31");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(edit, editTaskDescription, editTaskTime);
			
		} else if (index == 4) {
			
			Label search = new Label("SEARCH");
			search.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane searchKeyword = new GridPane();
			setHelpForCommand(searchKeyword, "      search tasks with keyword", "      search <keyword>", "      search school");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(search, searchKeyword);
			
		} else if (index == 5) {
			
			Label show = new Label("SHOW");
			show.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane showAll = new GridPane();
			setHelpForCommand(showAll, "      show all ongoing tasks", "      show all", "      show all");
			
			GridPane showDone = new GridPane();
			setHelpForCommand(showDone, "      show all done tasks", "      show done", "      show done");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(show, showAll, showDone);
			
		} else if (index == 6) {
			
			Label tag = new Label("TAG");
			tag.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane tagOneTask = new GridPane();
			setHelpForCommand(tagOneTask, "      tag one task", "      tag <taskID> <tag>", "      tag 1 important");
			
			GridPane untagOneTag = new GridPane();
			setHelpForCommand(untagOneTag, "      remove a tag from one task", "      untag <taskID> <tag>", "      untag 1 important");
			
			GridPane untagAllTags = new GridPane();
			setHelpForCommand(untagAllTags, "      remove all tags from one task", "      untag <taskID>", "      untag 1");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(tag, tagOneTask, untagOneTag, untagAllTags);
			
		} else {
			
			Label otherCommands = new Label("OTHER COMMANDS");
			otherCommands.setStyle("-fx-text-fill: tomato; -fx-padding: 0 0 0 15; -fx-font-size: 24");
			
			GridPane clear = new GridPane();
			setHelpForCommand(clear, "      remove all tasks", "      clear", "      clear");
			
			GridPane exit = new GridPane();
			setHelpForCommand(exit, "      exit uClear", "      exit", "      exit");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(otherCommands, clear, exit);
			
		}
	}
	
	private void displayTodayTasks() throws TaskInvalidDateException {
		setOnePageView(TODAY_TASKS_PAGE_INDEX, getTodayTaskList());
	}
	
	private void loadTaskListToController() throws TaskInvalidDateException {
		loadTaskList();
		taskList = getTaskList();
	}
	
	private void setOnePageView(int pageIndex) throws TaskInvalidDateException {
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<taskList.countUndone(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setStyle("-fx-padding: 5; -fx-font-size: 18; -fx-background-color: rgb(83,210,194);");
			
			setTaskFormat(taskLayout, i);
			
			GridPane taskDivision = new GridPane();
			taskDivision.setStyle("-fx-background-color: rgb(127,127,127)");
			setGridPaneSize(taskDivision, 850, 20);
			
			if (i == 0) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				setGridPaneSize(floatDivision, 850, 10);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].getChildren().add(taskLayout);
			page[pageIndex].getChildren().add(taskDivision);
			
			if (i == taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false))-1) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				setGridPaneSize(floatDivision, 850, 10);
				page[pageIndex].getChildren().add(floatDivision);
			}
		}
	}
	
	private void setOnePageView(int pageIndex, List<Task> specificTaskList) throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(pageIndex);
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<specificTaskList.size(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setStyle("-fx-padding: 5; -fx-font-size: 18; -fx-background-color: rgb(83,210,194);");
			taskLayout.getStyleClass().add("taskCard");
			
			setTaskFormat(taskLayout, specificTaskList.get(i), i);
			
			GridPane taskDivision = new GridPane();
			taskDivision.setStyle("-fx-background-color: rgb(127,127,127)");
			setGridPaneSize(taskDivision, 850, 20);
			
			if (i == 0) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				setGridPaneSize(floatDivision, 850, 10);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].getChildren().add(taskLayout);
			page[pageIndex].getChildren().add(taskDivision);
			
			if (i == taskList.indexOfFirstFloatingTask(specificTaskList)-1) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				setGridPaneSize(floatDivision, 850, 10);
				page[pageIndex].getChildren().add(floatDivision);
			}
		}
	}
	
	private void setTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		if (task.getType().equals(Type.FLOAT)) {
			setGridPaneSize(taskLayout, 850, 60);
			setFloatTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.DEADLINE)) {
			setGridPaneSize(taskLayout, 850, 90);
			setDeadlineTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.FIXED)) {
			setGridPaneSize(taskLayout, 850, 120);
			setFixedTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.REPEATED)) {
			setGridPaneSize(taskLayout, 850, 120);
			setRepeatedTaskFormat(taskLayout, task, index);
		} 
	}
	
	private void setTaskFormat(GridPane taskLayout, int index) throws TaskInvalidDateException {
		Task task = taskList.getTask(index);
		
		if (task.getType().equals(Type.FLOAT)) {
			setGridPaneSize(taskLayout, 850, 60);
			setFloatTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.DEADLINE)) {
			setGridPaneSize(taskLayout, 850, 90);
			setDeadlineTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.FIXED)) {
			setGridPaneSize(taskLayout, 850, 120);
			setFixedTaskFormat(taskLayout, task, index);
		} else if (task.getType().equals(Type.REPEATED)) {
			setGridPaneSize(taskLayout, 850, 120);
			setRepeatedTaskFormat(taskLayout, task, index);
		} 
	}
	
	private void setFloatTaskFormat(GridPane taskLayout, Task task, int index) {
		setDisplayIndex(taskLayout, index);
		setTaskType(taskLayout, task);
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setDeadlineTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index);
		setTaskType(taskLayout, task);
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setFixedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index);
		setTaskType(taskLayout, task);
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStartTime(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setRepeatedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index);
		setTaskType(taskLayout, task);
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setDeadline(taskLayout, task);
		setRepeatPeriod(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setDisplayIndex(GridPane taskLayout, int index) {
		Label displayIndex = new Label(Integer.toString(index+1));
		displayIndex.setPrefSize(50, 100);
		displayIndex.setStyle("-fx-text-fill: rgb(20,68,106); -fx-alignment: center; -fx-font-size: 40");
		GridPane.setConstraints(displayIndex, 0, 0, 1, 4);
		taskLayout.getChildren().add(displayIndex);
	}
	
	private void setTaskType(GridPane taskLayout, Task task) {
		Label type = new Label(task.getType().toString());
		type.setPrefSize(120, 50);
		type.setStyle("-fx-text-fill: rgb(245,56,85); -fx-alignment: center; -fx-background-color: rgb(88,239,121)");
		GridPane.setConstraints(type, 1, 0, 1, 1);
		taskLayout.getChildren().add(type);
	}
	
	private void setSpace(GridPane taskLayout) {
		Label space = new Label("  ");
		GridPane.setConstraints(space, 2, 0, 1, 1);
		taskLayout.getChildren().add(space);
	}
	
	private void setDescription(GridPane taskLayout, Task task) {
		Label description = new Label(task.getDescription());
		description.setStyle("-fx-text-fill: rgb(20,68,106); ");
		GridPane.setConstraints(description, 3, 0, 1, 1);
		taskLayout.getChildren().add(description);
	}
	
	private void setDeadline(GridPane taskLayout, Task task) throws TaskInvalidDateException {
		Label deadline = new Label();
		
		deadline.setText("Deadline: " + taskTimeFormat.format(task.getDeadline()));
		
		deadline.setStyle("-fx-text-fill: rgb(20,68,106)");
		
		if (task.getType().equals(Type.DEADLINE) || task.getType().equals(Type.REPEATED)) {
			GridPane.setConstraints(deadline, 1, 1, 3, 1);
		} else if (task.getType().equals(Type.FIXED)) {
			GridPane.setConstraints(deadline, 1, 2, 3, 1);
		}
		
		taskLayout.getChildren().add(deadline);
	}
	
	private void setStartTime(GridPane taskLayout, Task task) {
		FixedTask fixedTask = (FixedTask)task;
		Label startTime = new Label("Start Time: " + taskTimeFormat.format(fixedTask.getStartTime()));
		startTime.setStyle("-fx-text-fill: rgb(20,68,106)");
		GridPane.setConstraints(startTime, 1, 1, 3, 1);
		taskLayout.getChildren().add(startTime);
	}
	
	private void setRepeatPeriod(GridPane taskLayout, Task task) {
		RepeatedTask repeatedTask = (RepeatedTask)task;
		Label repeatPeriod = new Label(repeatedTask.getRepeatPeriod());
		repeatPeriod.setStyle("-fx-text-fill: rgb(20,68,106)");
		GridPane.setConstraints(repeatPeriod, 1, 2, 3, 1);
		taskLayout.getChildren().add(repeatPeriod);
	}
	
	private void setTags(GridPane taskLayout, Task task) {
		Type taskType = task.getType();
		HBox tagBox = new HBox();
		tagBox.setSpacing(20);
		if (task.getTags().size() > 0) {
			Label[] tags = new Label[task.getTags().size()];
			
			for (int j=0; j<task.getTags().size(); j++) {
				List<String> tagList = task.getTags();
				tags[j] = new Label(tagList.get(j));
				
				if (!tagColor.containsKey(tags[j].getText())) {
					tagColor.put(tags[j].getText(), DEFAULT_TAG_COLORS[colorPointer]);
					colorPointer = (colorPointer + 1) % 10;
				}
				tags[j].setStyle(CSS_BACKGROUND_COLOR + tagColor.get(tags[j].getText()) + "; -fx-text-fill: white; -fx-label-padding: 1 2 1 2; -fx-border-color: red;");
				
				tagBox.getChildren().add(tags[j]);
			}
			
		}
		
		if (taskType.equals(Type.FLOAT)) {
			GridPane.setConstraints(tagBox, 1, 1, 3, 1);
			taskLayout.getChildren().add(tagBox);
		} else if (taskType.equals(Type.DEADLINE)) {
			GridPane.setConstraints(tagBox, 1, 2, 3, 1);
			taskLayout.getChildren().add(tagBox);
		} else if (taskType.equals(Type.FIXED) || taskType.equals(Type.REPEATED)) {
			GridPane.setConstraints(tagBox, 1, 3, 3, 1);
			taskLayout.getChildren().add(tagBox);
		}
		
	}
	
	private void setGridPaneSize(GridPane gridPane, double width, double height) {
		gridPane.setPrefSize(width, height);
		gridPane.setMaxSize(width, height);
		gridPane.setMinSize(width, height);
	}	
	
	private void setRestTaskResponse() throws TaskInvalidDateException {
		if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
			List<Task> todayTasks = getTodayTaskList();
			if (todayTasks.size() > 1) {
				response.setText(String.format(MANY_TASKS_NOT_DONE, todayTasks.size()));
			} else if (todayTasks.size() == 1) {
				response.setText(ONE_TASK_NOT_DONE);
			} else {
				response.setText(ALL_TASKS_DONE);
			}
		} else if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
			if (taskList.countUndone() > 1) {
				response.setText(String.format(MANY_TASKS_NOT_DONE, taskList.countUndone()));
			} else if (taskList.countUndone() == 1) {
				response.setText(ONE_TASK_NOT_DONE);
			} else {
				response.setText(ALL_TASKS_DONE);
			}
		} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			if (taskList.countFinished() == 0) {
				response.setText("You haven't finished any tasks yet!");
			} else if (taskList.countFinished() == 1) {
				response.setText("Good! 1 task has been finished!");
			} else {
				response.setText("Good! " + taskList.countFinished() + " tasks have been finished!");
			}
		} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
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
		} else if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX){
			response.setText("Press \"F1\" - \"F8\" for more details.");
		} else {
			
		}
	}

	private String getUserInput() {
		return input.getText();
	}
	
	private String executeCommand(String command) {
		return Logic.readAndExecuteCommands(command);
	}
	
	private boolean isSpecialCommand() throws TaskInvalidDateException {		
		if (command.trim().toLowerCase().equals("exit")) {
			saveTaskList();
			Platform.exit();
		}
		
		if (command.trim().toLowerCase().equals("option") || command.trim().toLowerCase().equals("settings")) {
			Stage option = new Stage();
			Pane testWindow = new Pane();
			Label test = new Label("Haha, no settings here!");
			test.setStyle("-fx-text-fill: orangered; -fx-font-size: 15");
			testWindow.getChildren().add(test);
			testWindow.setStyle("-fx-background-color: black;");
			Scene optionScene = new Scene(testWindow, 300, 300);
			option.setTitle("Settings");
			option.getIcons().add(new Image("/view/Settings-icon.png"));
			option.setScene(optionScene);
			option.show();
			input.setText("");
			return true;
		}
		
		if (command.trim().toLowerCase().equals("fun")) {
			final Stage option = new Stage();
			Pane testWindow = new Pane();
			Label test = new Label("Like us on Facebook!");
			test.setStyle("-fx-text-fill: skyblue; -fx-font-size: 28");
			testWindow.getChildren().add(test);
			testWindow.setStyle("-fx-background-color: black");
			Scene optionScene = new Scene(testWindow, 300, 300);
			option.setTitle("Fun");
			option.setScene(optionScene);
			option.show();
			input.setText("");
			PauseTransition pause = new PauseTransition(Duration.seconds(3));
			pause.setOnFinished(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent event) {
			        option.hide();
			    }
			});
			pause.play();
			return true;
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
	
	private List<Task> getTodayTaskList() throws TaskInvalidDateException {
		List<Date> today = Logic.getDateList("show today");
		List<Task> todayTask = taskList.getDateRangeTask(today);
		
		return todayTask;
	}
	
	private void setTextField(String content) {
		input.setText(content);
	}
	
	private void setTextFieldEmpty() {
		setTextField("");
	}
	
	private void displayHelpCommand() throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(HELP_DOC_PAGE_INDEX);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		
		setHelpHomePage();
		
		setDisplayTitleText();
		setRestTaskResponse();
		listDisplay.requestFocus();
	}
	
	private void displaySearchCommand() throws TaskInvalidDateException {
		if (searchKey != null) {
			setOnePageView(SEARCH_RESULT_PAGE_INDEX, taskList.searchTaskByKeyword(searchKey));
		} else {
			listDisplay.setCurrentPageIndex(SEARCH_RESULT_PAGE_INDEX);
		}
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowAllCommand() throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(UNDONE_TASKS_PAGE_INDEX);
		setDisplayTitleText();
		setRestTaskResponse();
		setOnePageView(UNDONE_TASKS_PAGE_INDEX);
	}
	
	private void displayShowDoneCommand() throws TaskInvalidDateException {		
		setOnePageView(DONE_TASKS_PAGE_INDEX, taskList.getFinishedTasks());
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowTodayCommand() throws TaskInvalidDateException {
		List<Task> todayTask = getTodayTaskList();
		
		setOnePageView(TODAY_TASKS_PAGE_INDEX, todayTask);
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayOtherCommand() throws TaskInvalidDateException {
		if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
			setOnePageView(UNDONE_TASKS_PAGE_INDEX);
		} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			setOnePageView(DONE_TASKS_PAGE_INDEX, taskList.getFinishedTasks());
		} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			if (searchKey != null) {
				setOnePageView(SEARCH_RESULT_PAGE_INDEX, taskList.searchTaskByKeyword(searchKey));
			}
		}
		
		response.setText(feedback);
		response.setStyle("-fx-text-fill: rgb(68,217,117)");
		
		fadeOut.playFromStart();
		setDisplayTitleText();
	}
	
	private void analyseCommand(String command) throws TaskInvalidDateException {
		if (command.trim().length() == 4 && command.trim().toLowerCase().substring(0, 4).equals("help")) {
			displayHelpCommand();
		} else if (command.trim().length() > 6 && command.trim().toLowerCase().substring(0, 6).equals("search")) {
			searchKey = command.trim().substring(7);
			displaySearchCommand();
		} else if ((command.trim().length() == 8 && command.trim().toLowerCase().substring(0, 8).equals("show all")) 
				|| (command.trim().length() == 4 && command.trim().toLowerCase().substring(0, 4).equals("show"))) {
			taskList.setNotShowingDone();
			taskList.setShowDisplayListToFalse();
			displayShowAllCommand();
		} else if (command.trim().length() == 9 && command.trim().toLowerCase().substring(0, 9).equals("show done")) {
			taskList.setShowDisplayListToFalse();
			displayShowDoneCommand();
		} else if (command.trim().length() == 10 && command.trim().toLowerCase().substring(0, 10).equals("show today")
				|| command.trim().length() == 8 && command.trim().toLowerCase().substring(0, 8).equals("show tdy")) {
			displayShowTodayCommand();
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
				if (listDisplay.getCurrentPageIndex() != 2) {
					taskList.setNotShowingDone();
				}
				setTextFieldEmpty();
				saveTaskList();
				
				analyseCommand(command);
				
				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						try {
							setRestTaskResponse();
						} catch (TaskInvalidDateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						response.setStyle("-fx-font-size: 20;-fx-text-fill: #e51c23");
					}
					
				});
			}
		}
    }
	
	@FXML
	private void onKeyTyped(KeyEvent keyEvent) throws TaskInvalidDateException {
		if (keyEvent.getCharacter().equals("1")) {
			setDisplayTitleText();
			displayTodayTasks();
		}
		if (keyEvent.getCharacter().equals("2")) {
			taskList.setShowDisplayListToFalse();
			taskList.setNotShowingDone();
			displayShowAllCommand();
		}
		if (keyEvent.getCharacter().equals("3")) {
			displayShowDoneCommand();
		}
		if (keyEvent.getCharacter().equals("4")) {
			displaySearchCommand();
		}
		if (keyEvent.getCharacter().equals("5")) {
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
		
		if (keyEvent.getCharacter().equals("o")) {
			setTextField("option");
			input.requestFocus();
			input.selectEnd();
		}
		if (keyEvent.getCharacter().equals("f")) {
			setTextField("fun");
			input.requestFocus();
			input.selectEnd();
		}
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
			        		if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
			        			try {
			        				setDisplayTitleText();
									displayTodayTasks();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        		} else if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
			    				try {
			    					taskList.setShowDisplayListToFalse();
			    					taskList.setNotShowingDone();
									displayShowAllCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowDoneCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			    				try {
									displaySearchCommand();
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
			     public void handle(KeyEvent event) {
			        if (event.getCode() == KeyCode.RIGHT) {
	//		        	event.consume();
			        	boolean flag = true;
			        	if (flag) {
			        		flag = false;
			        		if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
			        			try {
			        				taskList.setShowDisplayListToFalse();
			    					taskList.setNotShowingDone();
									displayShowAllCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        		} else if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX) {
			    				try {
									displayHelpCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowDoneCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			    				try {
									displaySearchCommand();
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

	private void setEscKey() {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if (event.getCode() == KeyCode.ESCAPE) {
	//		        	event.consume();
			        	boolean flag = true;
			        	if (flag) {
			        		flag = false;
			        		
			        		if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX) {
			        			try {
									setHelpHomePage();
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
			        		if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX) {
			    				setHelpPage(index);
			    			}
			        	}
			        }
			     }
			});
		}

}
