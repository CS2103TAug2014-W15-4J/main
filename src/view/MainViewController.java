package view;

import controller.Logic;
import controller.UserInput.CMD;
import model.FixedTask;
import model.RepeatedTask;
import model.TaskList;
import model.Task;
import model.Task.Type;
import exception.TaskInvalidDateException;
import exception.TaskNoSuchTagException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

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
import javafx.geometry.Pos;
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
		"#009688",
		"#F44336",
		"#E91E63",
		"#673AB7",
		"#4CAF50",
		"#8BC34A",
		"#FF9800",
		"#795548",
		"#607D8B",
		"#FFC107",
		"#03A9F4",
		"#F44336"
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
	final static String ONE_OVERDUE_TASK = "Sign! 1 task overdue";
	final static String MANY_OVERDUE_TASKS = "Sign! %s tasks overdue";
	final static String NO_OVERDUE_TASK = "Congratulations! No overdue task!";
	
	final static String TITLE_TODAY_TASKS = "Today Tasks";
	final static String TITLE_PERIOD_TASKS = "Period Tasks";
	final static String TITLE_ALL_TASKS = "All Tasks";
	final static String TITLE_DONE_TASKS = "Done Tasks";
	final static String TITLE_OVERDUE_TASKS = "Overdue Tasks";
	final static String TITLE_TASKS_WITH_TAG = "Tasks With Specific Tag";
	final static String TITLE_SEARCH_RESULT = "Search Result";
	final static String TITLE_HELP_PAGE = "Help Document";
	
	final static int TOTAL_PAGE_NUM = 8;
	final static int TODAY_TASKS_PAGE_INDEX = 0;
	final static int PERIOD_TASKS_PAGE_INDEX = 1;
	final static int UNDONE_TASKS_PAGE_INDEX = 2;
	final static int DONE_TASKS_PAGE_INDEX = 3;
	final static int OVERDUE_TASKS_PAGE_INDEX = 4;
	final static int TASKS_WITH_TAG_PAGE_INDEX = 5;
	final static int SEARCH_RESULT_PAGE_INDEX = 6;
	final static int HELP_DOC_PAGE_INDEX = 7;
	
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
	String showTag;
	String showPeriod;
	
	private ArrayList<String> historyCommands;
	private int historyPointer;
	
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

	public MainViewController() throws IOException, TaskInvalidDateException, TaskNoSuchTagException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        fxmlLoader.load();
        
        setMainView();
	}
	
	private void setMainView() throws TaskInvalidDateException, TaskNoSuchTagException {
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
        initHistoryPointer();
	}
	
	public void initHistoryPointer() {
		historyPointer = 0;
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
		} else if (currentPageNum == PERIOD_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_PERIOD_TASKS);
		} else if (currentPageNum == UNDONE_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_ALL_TASKS);
		} else if (currentPageNum == DONE_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_DONE_TASKS);
		} else if (currentPageNum == OVERDUE_TASKS_PAGE_INDEX) {
			displayTitleText.setText(TITLE_OVERDUE_TASKS);
		} else if (currentPageNum == TASKS_WITH_TAG_PAGE_INDEX) {
			displayTitleText.setText(TITLE_TASKS_WITH_TAG);
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
		fadeOut.setCycleCount(5);
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
			page[i].setPrefHeight(listDisplay.getPrefHeight() - 50);
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			page[i].setStyle("-fx-background-color: white; -fx-padding: 0 0 0 20;");
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
		scrollPage[index].setFitToWidth(true);
		scrollPage[index].getStyleClass().add("taskListPane");
	}
	
	private void setFont() {
//	    wholePane.setStyle("-fx-font-family: Montserrat-Regular");
	    date.setStyle("-fx-font-size: 18");
	    response.setStyle("-fx-font-size: 20");
	    input.setStyle("-fx-font-size: 20");
	    displayTitleText.setStyle("-fx-font-size: 34");
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
	
	private void setHelpHomePage() throws TaskInvalidDateException, TaskNoSuchTagException {
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		setRestTaskResponse();
		
		commandInfo = new GridPane[8];
		fKeys = new Label[8];
		fKeysInfo = new Label[8];
		
		for (int i=0; i<7; i++) {
			
			commandInfo[i] = new GridPane();
			setHelpHBoxSize(i, 900, 40);
			
			fKeys[i] = new Label(F_KEYS[i].toString());
			fKeys[i].setStyle("-fx-padding: 0 0 0 50; -fx-font-size: 24; -fx-text-fill: #03a9f4");
			
			fKeysInfo[i] = new Label(F_KEY_COMMAND[i].toString());
			fKeysInfo[i].setStyle("-fx-padding: 0; -fx-font-size: 24; -fx-text-fill: rgb(0,0,0,87)");
			
			GridPane.setConstraints(fKeys[i], 0, 0);
			commandInfo[i].getChildren().add(0, fKeys[i]);
			GridPane.setConstraints(fKeysInfo[i], 1, 0);
			commandInfo[i].getChildren().add(1, fKeysInfo[i]);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[i]);
			
			GridPane.setMargin(fKeys[i], new Insets(5, 15, 5, 0));
			GridPane.setMargin(fKeysInfo[i], new Insets(5, 15, 5, 16));
			
		}
		commandInfo[7] = new GridPane();
		setHelpHBoxSize(7, 900, 40);
		
		fKeys[7] = new Label(F_KEYS[7].toString());
		fKeys[7].setStyle("-fx-padding: 0; -fx-font-size: 24; -fx-text-fill: #03a9f4");
		
		fKeysInfo[7] = new Label("OTHER COMMANDS");
		fKeysInfo[7].setStyle("-fx-padding: 0; -fx-font-size: 24; -fx-text-fill: rgb(0,0,0,87)");
		
		GridPane.setConstraints(fKeys[7], 0, 0);
		commandInfo[7].getChildren().add(fKeys[7]);
		GridPane.setConstraints(fKeysInfo[7], 1, 0);
		commandInfo[7].getChildren().add(fKeysInfo[7]);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[7]);
		
		GridPane.setMargin(fKeys[7], new Insets(5, 15, 5, 50));
		GridPane.setMargin(fKeysInfo[7], new Insets(5, 15, 5, 16));
	}
	
	private void setHelpHBoxSize(int index, double width, double height) {
		commandInfo[index].setPrefSize(width, height);
		commandInfo[index].setMaxSize(width, height);
		commandInfo[index].setMinSize(width, height);
	}
	
	private void setHelpForCommand(GridPane command, String descriptionString, String structureString, String exampleString) {
		Label description = new Label("Description");
		description.setStyle("-fx-text-fill:  #03a9f4");
		Label structure = new Label("Structure");
		structure.setStyle("-fx-text-fill:  #03a9f4");
		Label example = new Label("Example");
		example.setStyle("-fx-text-fill:  #03a9f4");
		
		command.setStyle("-fx-padding: 0 5 10 50; -fx-font-size: 18");
		setGridPaneSize(command, 900, 140);
		Label descriptionContent = new Label(descriptionString);
		descriptionContent.setStyle("-fx-text-fill: rgb(0,0,0,87)");
		Label structureContent = new Label(structureString);
		structureContent.setStyle("-fx-text-fill: rgb(0,0,0,87)");
		Label exampleContent = new Label(exampleString);
		exampleContent.setStyle("-fx-text-fill: rgb(0,0,0,87)");
		
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
		page[HELP_DOC_PAGE_INDEX].setSpacing(20);
		if (index == 0) {
			
			Label add = new Label("ADD");
			add.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
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
			delete.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
			GridPane deleteOneTask = new GridPane();
			setHelpForCommand(deleteOneTask, "      delete one task", "      delete <taskID>", "      delete 1");
			
			GridPane deleteManyTasks = new GridPane();
			setHelpForCommand(deleteManyTasks, "      delete many tasks", "      delete <taskID_1> <taskID_2> ...", "      delete 1 2 3");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(delete, deleteOneTask, deleteManyTasks);
			
		} else if (index == 2) {
			
			Label done = new Label("DONE");
			done.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
			GridPane doneOneTask = new GridPane();
			setHelpForCommand(doneOneTask, "      mark one task done", "      done <taskID>", "      done 1");
			
			GridPane doneManyTasks = new GridPane();
			setHelpForCommand(doneManyTasks, "      mark many tasks done", "      done <taskID_1> <taskID_2> ...", "      done 1 2 3");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(done, doneOneTask, doneManyTasks);
			
		} else if (index == 3) {
			
			Label edit = new Label("EDIT");
			edit.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
			GridPane editTaskDescription = new GridPane();
			setHelpForCommand(editTaskDescription, "      edit task description of one task", "      edit <taskID> <new description>", "      edit 1 sleep");
			
			GridPane editTaskTime = new GridPane();
			setHelpForCommand(editTaskTime, "      edit task time of one task", "      edit <taskID> <new time>", "      edit 1 Oct 31");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(edit, editTaskDescription, editTaskTime);
			
		} else if (index == 4) {
			
			Label search = new Label("SEARCH");
			search.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
			GridPane searchKeyword = new GridPane();
			setHelpForCommand(searchKeyword, "      search tasks with keyword", "      search <keyword>", "      search school");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(search, searchKeyword);
			
		} else if (index == 5) {
			
			Label show = new Label("SHOW");
			show.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
			GridPane showAll = new GridPane();
			setHelpForCommand(showAll, "      show all ongoing tasks", "      show all", "      show all");
			
			GridPane showDone = new GridPane();
			setHelpForCommand(showDone, "      show all done tasks", "      show done", "      show done");
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(show, showAll, showDone);
			
		} else if (index == 6) {
			
			Label tag = new Label("TAG");
			tag.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
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
			otherCommands.setStyle("-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24");
			
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
	
	private void displayPeriodTasks() throws TaskInvalidDateException {
		setOnePageView(PERIOD_TASKS_PAGE_INDEX, getPeriodTaskList(showPeriod));
	}
	
	private List<Task> getPeriodTaskList(String userCommand) throws TaskInvalidDateException {
		List<Date> periodDate = Logic.getDateList(userCommand);
		List<Task> periodTasks = taskList.getDateRangeTask(periodDate);
		
		return periodTasks;
	}
	
	private void loadTaskListToController() throws TaskInvalidDateException {
		loadTaskList();
		taskList = getTaskList();
	}
	
	private void setOnePageView(int pageIndex) throws TaskInvalidDateException {
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<taskList.countUndone(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.getStyleClass().add("taskCard");
      
			setTaskFormat(taskLayout, i);
			
			GridPane taskDivision = new GridPane();
			taskDivision.setStyle("-fx-background-color: rgb(127,127,127)");
			setGridPaneSize(taskDivision, 850, 20);
			
			if (i == 0) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) Due Soon");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].setSpacing(10);
			page[pageIndex].getChildren().add(taskLayout);
			
			if (i == taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false))-1) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) Due Soon");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
		}
	}
	
	private void setOnePageView(int pageIndex, List<Task> specificTaskList) throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(pageIndex);
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<specificTaskList.size(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.getStyleClass().add("taskCard");
            
			setTaskFormat(taskLayout, specificTaskList.get(i), i);
			
			GridPane taskDivision = new GridPane();
			taskDivision.setStyle("-fx-background-color: rgb(127,127,127)");
			setGridPaneSize(taskDivision, 850, 20);
			
			if (i == 0) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) Due Soon");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].setSpacing(10);
			page[pageIndex].getChildren().add(taskLayout);
			
			if (i == taskList.indexOfFirstFloatingTask(specificTaskList)-1) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) To do");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
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
	

	private void setFloatTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, false, task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setDeadlineTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setFixedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setStartTime(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setRepeatedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setDeadline(taskLayout, task);
		setRepeatPeriod(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	private void setDisplayIndex(GridPane taskLayout, int index, boolean isOverdue, boolean isDone) {
		Label displayIndex = new Label(Integer.toString(index+1));
	
		displayIndex.setMaxHeight(140);
		displayIndex.setMinWidth(50);
		displayIndex.setMaxWidth(50);
		if (isOverdue) {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#F44336;");
		} else if (isDone) {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#4CAF50;");
		} else {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#29b6f6;");
		}
		
		GridPane.setConstraints(displayIndex, 0, 0, 1, 5);
		taskLayout.getChildren().add(displayIndex);
	}
	
	private void setSpace(GridPane taskLayout) {
		Label space = new Label("  ");
		GridPane.setConstraints(space, 2, 0, 1, 1);
		taskLayout.getChildren().add(space);
	}
	
	private void setDescription(GridPane taskLayout, Task task) {
		Label description = new Label(task.getDescription());
		description.setPrefSize(650, 30);
		description.setMaxHeight(30);
		description.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px ; -fx-font-size:24;");
		GridPane.setConstraints(description, 1, 0, 1, 1);
		taskLayout.getChildren().add(description);
	}
	
	private void setStatus(GridPane taskLayout, Task task) throws TaskInvalidDateException {
		Label status = new Label();
		status.setPrefWidth(150);
		status.setMaxHeight(140);
		status.setMinWidth(100);
		status.setMaxWidth(150);
		status.setAlignment(Pos.CENTER);
		status.getStyleClass().add("status-card");
		
		if (task.getType().equals(Type.FLOAT)) {
			if (task.getIsDone()) {
				status.setText("DONE");
				status.setStyle("-fx-background-color: #4CAF50;");
			} else {
				status.setText("ONGOING");
				status.setStyle("-fx-background-color: #29b6f6;");
			}
		} else {
			task.checkOverdue();
			if (task.getIsOverdue()) {
				status.setText("OVERDUE");
				status.setStyle("-fx-background-color: #F44336;");
			} else {
				if (task.getIsDone()) {
					status.setText("DONE");
					status.setStyle("-fx-background-color: #4CAF50;");
				} else {
					status.setText("ONGOING");
					status.setStyle("-fx-background-color: #29b6f6;");
				}
			}
		}
		GridPane.setConstraints(status, 2, 0, 1, 5);
		taskLayout.getChildren().add(status);
	}
	
	private void setDeadline(GridPane taskLayout, Task task) throws TaskInvalidDateException {
		Label deadline = new Label();
		
		deadline.setText("Deadline: " + taskTimeFormat.format(task.getDeadline()));
		deadline.setPrefSize(650, 30);
		deadline.setMaxHeight(30);
		deadline.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px; -fx-font-size:16;");
		
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
		
		startTime.setPrefSize(650, 30);
		startTime.setMaxHeight(30);
		startTime.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px; -fx-font-size:16;");
		
		GridPane.setConstraints(startTime, 1, 1, 3, 1);
		taskLayout.getChildren().add(startTime);
	}
	
	private void setRepeatPeriod(GridPane taskLayout, Task task) {
		RepeatedTask repeatedTask = (RepeatedTask)task;
		Label repeatPeriod = new Label("Repeat Period: " + repeatedTask.getRepeatPeriod());
		repeatPeriod.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px; -fx-font-size:16;");
		repeatPeriod.setPrefSize(650, 30);
		repeatPeriod.setMaxHeight(30);
		GridPane.setConstraints(repeatPeriod, 1, 2, 3, 1);
		taskLayout.getChildren().add(repeatPeriod);
	}
	
	private void setTags(GridPane taskLayout, Task task) {
		Type taskType = task.getType();
		HBox tagBox = new HBox();
		tagBox.setStyle("-fx-padding: 0 0 0 16px; -fx-valignment: center;");
		tagBox.setAlignment(Pos.CENTER_LEFT);
		tagBox.setSpacing(20);
		tagBox.setPrefSize(650, 30);
		tagBox.setMaxHeight(30);
		if (task.getTags().size() > 0) {
			Label[] tags = new Label[task.getTags().size()];
			
			for (int j=0; j<task.getTags().size(); j++) {
				List<String> tagList = task.getTags();
				tags[j] = new Label(tagList.get(j));
				
				if (!tagColor.containsKey(tags[j].getText())) {
					tagColor.put(tags[j].getText(), DEFAULT_TAG_COLORS[colorPointer]);
					colorPointer = (colorPointer + 1) % 10;
				}
				tags[j].getStyleClass().add("tags");
				tags[j].setStyle(CSS_BACKGROUND_COLOR + tagColor.get(tags[j].getText()));
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
	
	private void setRestTaskResponse() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
			List<Task> todayTasks = getTodayTaskList();
			if (todayTasks.size() > 1) {
				response.setText(String.format(MANY_TASKS_NOT_DONE, todayTasks.size()));
			} else if (todayTasks.size() == 1) {
				response.setText(ONE_TASK_NOT_DONE);
			} else {
				response.setText(ALL_TASKS_DONE);
			}
		} else if (listDisplay.getCurrentPageIndex() == PERIOD_TASKS_PAGE_INDEX) {
			if (showPeriod == null) {
				response.setText("No Period Set!");
			} else {
				List<Task> periodTasks = getPeriodTaskList(showPeriod);
				if (periodTasks.size() > 1) {
					response.setText(String.format(MANY_TASKS_NOT_DONE, periodTasks.size()));
				} else if (periodTasks.size() == 1) {
					response.setText(ONE_TASK_NOT_DONE);
				} else {
					response.setText(ALL_TASKS_DONE);
				}
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
		} else if (listDisplay.getCurrentPageIndex() == OVERDUE_TASKS_PAGE_INDEX) {
			if (taskList.getOverdueTask().size() > 1) {
				response.setText(String.format(MANY_OVERDUE_TASKS, taskList.getOverdueTask().size()));
			} else if (taskList.getOverdueTask().size() == 1) {
				response.setText(ONE_OVERDUE_TASK);
			} else {
				response.setText(NO_OVERDUE_TASK);
			}
		} else if (listDisplay.getCurrentPageIndex() == TASKS_WITH_TAG_PAGE_INDEX) {
			if (showTag == null) {
				response.setText("No Task With This Tag!");
			} else {
				if (taskList.isTagContained(showTag)) {
					List<Task> tagTasks = getTagTaskList(showTag);
					if (tagTasks.size() > 1) {
						response.setText(String.format(MANY_TASKS_NOT_DONE, tagTasks.size()));
					} else if (tagTasks.size() == 1) {
						response.setText(ONE_TASK_NOT_DONE);
					} else {
						response.setText(ALL_TASKS_DONE);
					}
				} else {
					response.setText("No Such Tag!");
				}
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
		List<Task> todayTasks = taskList.getDateRangeTask(today);
		
		return todayTasks;
	}
	
	private List<Task> getTagTaskList(String tag) throws TaskNoSuchTagException {
		List<Task> tagTasks = taskList.getTasksWithTag(tag);
		
		return tagTasks;
	}
	
	private void setTextField(String content) {
		input.setText(content);
	}
	
	private void setTextFieldEmpty() {
		setTextField("");
	}
	
	private void displayHelpCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		listDisplay.setCurrentPageIndex(HELP_DOC_PAGE_INDEX);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		
		setHelpHomePage();
		
		setDisplayTitleText();
		setRestTaskResponse();
		listDisplay.requestFocus();
	}
	
	private void displaySearchCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (searchKey != null) {
			setOnePageView(SEARCH_RESULT_PAGE_INDEX, taskList.searchTaskByKeyword(searchKey));
		} else {
			listDisplay.setCurrentPageIndex(SEARCH_RESULT_PAGE_INDEX);
		}
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowAllCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		listDisplay.setCurrentPageIndex(UNDONE_TASKS_PAGE_INDEX);
		setDisplayTitleText();
		setRestTaskResponse();
		setOnePageView(UNDONE_TASKS_PAGE_INDEX);
	}
	
	private void displayShowDoneCommand() throws TaskInvalidDateException, TaskNoSuchTagException {		
		setOnePageView(DONE_TASKS_PAGE_INDEX, taskList.getFinishedTasks());
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowOverdueCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		setOnePageView(OVERDUE_TASKS_PAGE_INDEX, taskList.getOverdueTask());
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowTagCommand() throws TaskNoSuchTagException, TaskInvalidDateException {
		if (showTag != null) {
			if (taskList.isTagContained(showTag)) {
				List<Task> tagTasks = taskList.getTasksWithTag(showTag);
				setOnePageView(TASKS_WITH_TAG_PAGE_INDEX, tagTasks);
			} else {
				listDisplay.setCurrentPageIndex(TASKS_WITH_TAG_PAGE_INDEX);
			}
		} else {
			listDisplay.setCurrentPageIndex(TASKS_WITH_TAG_PAGE_INDEX);
		}
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowPeriodCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (showPeriod != null) {
			displayPeriodTasks();
		} else {
			listDisplay.setCurrentPageIndex(PERIOD_TASKS_PAGE_INDEX);
		}
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayShowTodayCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		List<Task> todayTasks = getTodayTaskList();
		
		setOnePageView(TODAY_TASKS_PAGE_INDEX, todayTasks);
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	private void displayOtherCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
			displayTodayTasks();
		} else if (listDisplay.getCurrentPageIndex() == PERIOD_TASKS_PAGE_INDEX) {
			displayPeriodTasks();
		} else if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
			setOnePageView(UNDONE_TASKS_PAGE_INDEX);
		} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			setOnePageView(DONE_TASKS_PAGE_INDEX, taskList.getFinishedTasks());
		} else if (listDisplay.getCurrentPageIndex() == OVERDUE_TASKS_PAGE_INDEX) {
			setOnePageView(OVERDUE_TASKS_PAGE_INDEX, taskList.getOverdueTask());
		} else if (listDisplay.getCurrentPageIndex() == TASKS_WITH_TAG_PAGE_INDEX) {
			setOnePageView(TASKS_WITH_TAG_PAGE_INDEX, taskList.getTasksWithTag(showTag));
		} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			if (searchKey != null) {
				setOnePageView(SEARCH_RESULT_PAGE_INDEX, taskList.searchTaskByKeyword(searchKey));
			}
		}
		
		response.setText(feedback);
		
		response.setStyle("-fx-text-fill: #4CAF50;");
		
		fadeOut.playFromStart();
		setDisplayTitleText();
	}
	
	private void analyseCommand(String command) throws TaskInvalidDateException, TaskNoSuchTagException {
		if (command.trim().length() == 4 && command.trim().toLowerCase().substring(0, 4).equals("help")) {
			displayHelpCommand();
		} else if (command.trim().length() > 6 && command.trim().toLowerCase().substring(0, 6).equals("search")) {
			searchKey = command.trim().toLowerCase().substring(7);
			displaySearchCommand();
		} else if (command.trim().length() == 12 && command.trim().toLowerCase().substring(0, 12).equals("show overdue")) {
			displayShowOverdueCommand();
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
		} else if (command.trim().length() > 4 && command.trim().toLowerCase().substring(0, 4).equals("show")) {
			if (!isShowDateCommand(command)) {
				showTag = command.trim().toLowerCase().substring(5);
				displayShowTagCommand();
			} else {
				showPeriod = command;
				displayShowPeriodCommand();
			}
		// except show, search and help
		} else {
			displayOtherCommand();
		}
	}
	
	private boolean isShowDateCommand(String userCommand) {
		return Logic.isShowDateCommand(userCommand);
	}
	
	@FXML
    private void onEnter() throws TaskInvalidDateException, TaskNoSuchTagException {
		command = getUserInput();
		historyCommands.add(command);
		historyPointer = historyPointer + 1;
		
		if (!command.equals("")) {			
			
			if (!isSpecialCommand()) {
				feedback = executeCommand(command);
				taskList = getTaskList();
				if (listDisplay.getCurrentPageIndex() != DONE_TASKS_PAGE_INDEX) {
					taskList.setNotShowingDone();
				}
				setTextFieldEmpty();
				saveTaskList();
				
				analyseCommand(command);
				
				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						try {
							try {
								setRestTaskResponse();
							} catch (TaskNoSuchTagException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
	private void onKeyTyped(KeyEvent keyEvent) throws TaskInvalidDateException, TaskNoSuchTagException {
		if (keyEvent.getCharacter().equals("1")) {
			displayShowTodayCommand();
		}
		if (keyEvent.getCharacter().equals("2")) {
			displayShowPeriodCommand();
		}
		if (keyEvent.getCharacter().equals("3")) {
			taskList.setShowDisplayListToFalse();
			taskList.setNotShowingDone();
			displayShowAllCommand();
		}
		if (keyEvent.getCharacter().equals("4")) {
			displayShowDoneCommand();
		}
		if (keyEvent.getCharacter().equals("5")) {
			displayShowOverdueCommand();
		}
		if (keyEvent.getCharacter().equals("6")) {
			displayShowTagCommand();
		}
		if (keyEvent.getCharacter().equals("7")) {
			displaySearchCommand();
		}
		if (keyEvent.getCharacter().equals("8")) {
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
	
	private void setUpKey() {
		input.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.UP) {
					boolean flag = true;
					if (flag) {
						flag = false;
						if (!historyCommands.isEmpty() && historyPointer != 0) {
							
							historyPointer = historyPointer - 1;
							input.setText(historyCommands.get(historyPointer));
							
						}
					}
				}
			}
		});
	}

	private void setLeftKey() {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if (event.getCode() == KeyCode.LEFT) {
	//		        	event.consume();
			        	boolean flag = true;
			        	if (flag) {
			        		flag = false;
			        		if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
			        			try {
									displayShowTodayCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        		} else if (listDisplay.getCurrentPageIndex() == PERIOD_TASKS_PAGE_INDEX) {
			        			try {
									displayShowPeriodCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
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
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowDoneCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == OVERDUE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowOverdueCommand();
								} catch (TaskInvalidDateException
										| TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == TASKS_WITH_TAG_PAGE_INDEX) {
			    				try {
									displayShowTagCommand();
								} catch (TaskNoSuchTagException
										| TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			    				try {
									displaySearchCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
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
			        		if (listDisplay.getCurrentPageIndex() == PERIOD_TASKS_PAGE_INDEX) {
			        			try {
									displayShowPeriodCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
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
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        		} else if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX) {
			    				try {
									displayHelpCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowDoneCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == OVERDUE_TASKS_PAGE_INDEX) {
			    				try {
									displayShowOverdueCommand();
								} catch (TaskInvalidDateException
										| TaskNoSuchTagException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == TASKS_WITH_TAG_PAGE_INDEX) {
			    				try {
									displayShowTagCommand();
								} catch (TaskNoSuchTagException
										| TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    			} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
			    				try {
									displaySearchCommand();
								} catch (TaskInvalidDateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TaskNoSuchTagException e) {
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
								} catch (TaskNoSuchTagException e) {
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
