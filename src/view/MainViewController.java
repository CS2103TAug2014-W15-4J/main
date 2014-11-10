package view;

import controller.Logic;
import controller.UserInput.CMD;
import log.ULogger;
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
import javafx.scene.control.OverrunStyle;
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

//@author A0119414L
/**
 * This class is a controller which is to control the view displayed in MainView.
 * 
 * @author Wang Zhipeng
 *
 */
public class MainViewController extends GridPane{
	
	private static final String[] DEFAULT_TAG_COLORS = {
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
	
	private static final KeyCode[] F_KEYS = {
		KeyCode.F1,
		KeyCode.F2,
		KeyCode.F3,
		KeyCode.F4,
		KeyCode.F5,
		KeyCode.F6,
		KeyCode.F7,
		KeyCode.F8,
	};
	
	private static final CMD[] F_KEY_COMMAND = {
		CMD.ADD,
		CMD.DELETE,
		CMD.DONE,
		CMD.EDIT,
		CMD.SEARCH,
		CMD.SHOW,
		CMD.TAG,
	};
	
	private static final String CSS_BACKGROUND_COLOR = "-fx-background-color: ";
	private static final String FX_COLOR_RGB = "rgb(%s, %s, %s)";
	private static final String FXML_FILE_NAME = "MainView.fxml";
	private static final String ONE_TASK_NOT_DONE = "Oops! 1 task should be done!";
	private static final String MANY_TASKS_NOT_DONE = "Oops! %s tasks should be done!";
	private static final String ALL_TASKS_DONE = "Good! All tasks are done!";
	private static final String ONE_OVERDUE_TASK = "Sigh! 1 task overdue";
	private static final String MANY_OVERDUE_TASKS = "Sigh! %s tasks overdue";
	private static final String NO_OVERDUE_TASK = "Congratulations! No overdue task!";
	
	private static final String TITLE_TODAY_TASKS = "Today";
	private static final String TITLE_PERIOD_TASKS = "Sometime";
	private static final String TITLE_ALL_TASKS = "All Task";
	private static final String TITLE_DONE_TASKS = "Done";
	private static final String TITLE_OVERDUE_TASKS = "Overdue";
	private static final String TITLE_TASKS_WITH_TAG = "Tag";
	private static final String TITLE_SHOW_TASKS_WITH_TAG = "Showing Tasks With Tag:   <%s>";
	private static final String TITLE_SHOW_TASKS_IN_PERIOD = "Showing Tasks Due During:   \"%s\" to \"%s\"";
	private static final String TITLE_SEARCH_RESULT = "Search";
	private static final String TITLE_SHOW_SEARCH_RESULT = "Searching Result For:   \"%s\"";
	private static final String TITLE_HELP_PAGE = "Help";
	private static final String TITLE_HELP_COMMAND = "Press \"Esc\" to return";
	
	private static final String DISPLAY_HELP_OTHER_COMMAND = "OTHER COMMANDS";
	private static final String DISPLAY_HELP_DISCRIPTION = "Description";
	private static final String DISPLAY_HELP_STRUCTURE = "Structure";
	private static final String DISPLAY_HELP_EXAMPLE = "Example";
	
	private static final String[] DISPLAY_HELP_COMMAND = {
		"ADD",
		"DELETE",
		"DONE",
		"EDIT",
		"SEARCH",
		"SHOW",
		"TAG"
	};
	
	private static final String DISPLAY_HELP_ADD_FLOATING_TASK_DESCRIPTION = "      add a floating task";
	private static final String DISPLAY_HELP_ADD_FLOATING_TASK_STRUCTURE = "      add <description>";
	private static final String DISPLAY_HELP_ADD_FLOATING_TASK_EXAMPLE = "      add do homework";
	
	private static final String DISPLAY_HELP_ADD_DEADLINE_TASK_DESCRIPTION = "      add a deadline task";
	private static final String DISPLAY_HELP_ADD_DEADLINE_TASK_STRUCTURE = "      add <description> by <time/date>";
	private static final String DISPLAY_HELP_ADD_DEADLINE_TASK_EXAMPLE = "      add do homework by Oct 31";
	
	private static final String DISPLAY_HELP_ADD_REPEATED_TASK_DESCRIPTION = "      add a repeated task";
	private static final String DISPLAY_HELP_ADD_REPEATED_TASK_STRUCTURE = "      add <description> every <time/date> <period>";
	private static final String DISPLAY_HELP_ADD_REPEATED_TASK_EXAMPLE = "      add do homework every week";
	
	private static final String DISPLAY_HELP_ADD_FIXED_TASK_DESCRIPTION = "      add a fixed task";
	private static final String DISPLAY_HELP_ADD_FIXED_TASK_STRUCTURE = "      add <description> <time/date1> to <time/date2>";
	private static final String DISPLAY_HELP_ADD_FIXED_TASK_EXAMPLE = "      add do homework Nov 10 to Nov 20";
	
	private static final String DISPLAY_HELP_DELETE_ONE_TASK_DESCRIPTION = "      delete one task";
	private static final String DISPLAY_HELP_DELETE_ONE_TASK_STRUCTURE = "      delete <taskID>";
	private static final String DISPLAY_HELP_DELETE_ONE_TASK_EXAMPLE = "      delete 1";
	
	private static final String DISPLAY_HELP_DELETE_MANY_TASKS_DESCRIPTION = "      delete many tasks";
	private static final String DISPLAY_HELP_DELETE_MANY_TASKS_STRUCTURE = "      delete <taskID_1> <taskID_2> ...";
	private static final String DISPLAY_HELP_DELETE_MANY_TASKS_EXAMPLE = "      delete 1 2 3";
	
	private static final String DISPLAY_HELP_DONE_ONE_TASK_DESCRIPTION = "      mark one task done";
	private static final String DISPLAY_HELP_DONE_ONE_TASK_STRUCTURE = "      done <taskID>";
	private static final String DISPLAY_HELP_DONE_ONE_TASK_EXAMPLE = "      done 1";
	
	private static final String DISPLAY_HELP_DONE_MANY_TASKS_DESCRIPTION = "      mark many tasks done";
	private static final String DISPLAY_HELP_DONE_MANY_TASKS_STRUCTURE = "      done <taskID_1> <taskID_2> ...";
	private static final String DISPLAY_HELP_DONE_MANY_TASKS_EXAMPLE = "      done 1 2 3";
	
	private static final String DISPLAY_HELP_EDIT_DESCRIPTION_DESCRIPTION = "      edit task description of one task";
	private static final String DISPLAY_HELP_EDIT_DESCRIPTION_STRUCTURE = "      edit <taskID> <new description>";
	private static final String DISPLAY_HELP_EDIT_DESCRIPTION_EXAMPLE = "      edit 1 sleep";
	
	private static final String DISPLAY_HELP_EDIT_TIME_DESCRIPTION = "      edit task time of one task";
	private static final String DISPLAY_HELP_EDIT_TIME_STRUCTURE = "      edit <taskID> <new time>";
	private static final String DISPLAY_HELP_EDIT_TIME_EXAMPLE = "      edit 1 Oct 31";
	
	private static final String DISPLAY_HELP_SEARCH_DESCRIPTION = "      search tasks with keyword";
	private static final String DISPLAY_HELP_SEARCH_STRUCTURE = "      search <keyword>";
	private static final String DISPLAY_HELP_SEARCH_EXAMPLE = "      search school";
	
	private static final String DISPLAY_HELP_SHOW_ALL_DESCRIPTION = "      show all ongoing tasks";
	private static final String DISPLAY_HELP_SHOW_ALL_STRUCTURE = "      show all";
	private static final String DISPLAY_HELP_SHOW_ALL_EXAMPLE = "      show all";
	
	private static final String DISPLAY_HELP_SHOW_DONE_DESCRIPTION = "      show all done tasks";
	private static final String DISPLAY_HELP_SHOW_DONE_STRUCTURE = "      show done";
	private static final String DISPLAY_HELP_SHOW_DONE_EXAMPLE = "      show done";
	
	private static final String DISPLAY_HELP_TAG_ONE_DESCRIPTION = "      tag one task";
	private static final String DISPLAY_HELP_TAG_ONE_STRUCTURE = "      tag <taskID> <tag>";
	private static final String DISPLAY_HELP_TAG_ONE_EXAMPLE = "      tag 1 important";
	
	private static final String DISPLAY_HELP_UNTAG_ONE_DESCRIPTION = "      remove a tag from one task";
	private static final String DISPLAY_HELP_UNTAG_ONE_STRUCTURE = "      untag <taskID> <tag>";
	private static final String DISPLAY_HELP_UNTAG_ONE_EXAMPLE = "      untag 1 important";
	
	private static final String DISPLAY_HELP_UNTAG_ALL_DESCRIPTION = "      remove all tags from one task";
	private static final String DISPLAY_HELP_UNTAG_ALL_STRUCTURE = "      untag <taskID>";
	private static final String DISPLAY_HELP_UNTAG_ALL_EXAMPLE = "      untag 1";
	
	private static final String DISPLAY_HELP_CLEAR_DESCRIPTION = "      remove all tasks";
	private static final String DISPLAY_HELP_CLEAR_STRUCTURE = "      clear";
	private static final String DISPLAY_HELP_CLEAR_EXAMPLE = "      clear";
	
	private static final String DISPLAY_HELP_EXIT_DESCRIPTION = "      exit uClear";
	private static final String DISPLAY_HELP_EXIT_STRUCTURE = "      exit";
	private static final String DISPLAY_HELP_EXIT_EXAMPLE = "      exit";
	
	private static final String DISPLAY_TITLE_TASK_DUE_SOON = "Task(s) Due Soon";
	private static final String DISPLAY_TITLE_TASK_TO_DO = "Task(s) To Do";
	
	private static final int TOTAL_PAGE_NUM = 8;
	private static final int TODAY_TASKS_PAGE_INDEX = 0;
	private static final int PERIOD_TASKS_PAGE_INDEX = 1;
	private static final int UNDONE_TASKS_PAGE_INDEX = 2;
	private static final int DONE_TASKS_PAGE_INDEX = 3;
	private static final int OVERDUE_TASKS_PAGE_INDEX = 4;
	private static final int TASKS_WITH_TAG_PAGE_INDEX = 5;
	private static final int SEARCH_RESULT_PAGE_INDEX = 6;
	private static final int HELP_DOC_PAGE_INDEX = 7;
	
	private static final String FORMAT_TASK_TIME = "MMM dd, EE  HH : mm";
	private static final String FORMAT_DATE = "MMM dd  HH : mm : ss";
	private static final String FORMAT_DATE_FOR_SHOW = "MMM dd";
	
	private static final int FADE_TIME = 400;
	private static final double FADE_FROM_VALUE = 1.0;
	private static final double FADE_TO_VALUE = 0.65;
	private static final int FADE_CYCLES = 4;
	private static final boolean FADE_AUTO_REVERSE = true;
	private static final int INITIAL_POSITION = 0;
	
	private static final double DIFFERENCE_HEIGHT = 66;
	private static final int TOTAL_FKEYS = 8;
	private static final int F_KEY_ADD = 0;
	private static final int F_KEY_DELETE = 1;
	private static final int F_KEY_DONE = 2;
	private static final int F_KEY_EDIT = 3;
	private static final int F_KEY_SEARCH = 4;
	private static final int F_KEY_SHOW = 5;
	private static final int F_KEY_TAG = 6;
	private static final int F_KEY_OTHER_COMMAND = 7;
	
	private static final String STYLE_PAGE = "-fx-background-color: white; -fx-padding: 0 0 0 20;";
	private static final String STYLE_FONT_DATE = "-fx-font-size: 18";
	private static final String STYLE_FONT_RESPONSE = "-fx-font-size: 20";
	private static final String STYLE_FONT_INPUT = "-fx-font-size: 20";
	private static final String STYLE_FONT_DISPLAYTITLETEXT = "-fx-font-size: 34";
	private static final String STYLE_F_KEYS = "-fx-padding: 0 0 0 50; -fx-font-size: 24; -fx-text-fill: #03a9f4";
	private static final String STYLE_F_KEYS_INFO = "-fx-padding: 0; -fx-font-size: 24; -fx-text-fill: rgb(0,0,0,87)";
	
	private static final String STYLE_HELP_DESCRIPTION = "-fx-text-fill:  #03a9f4";
	private static final String STYLE_HELP_STRUCTURE = "-fx-text-fill:  #03a9f4";
	private static final String STYLE_HELP_EXAMPLE = "-fx-text-fill:  #03a9f4";
	private static final String STYLE_HELP_GRIDPANE = "-fx-padding: 0 5 10 50; -fx-font-size: 18";
	private static final String STYLE_HELP_DESCRIPTION_CONTENT = "-fx-text-fill: rgb(0,0,0,87)";
	private static final String STYLE_HELP_STRUCTURE_CONTENT = "-fx-text-fill: rgb(0,0,0,87)";
	private static final String STYLE_HELP_EXAMPLE_CONTENT = "-fx-text-fill: rgb(0,0,0,87)";
	
	private static final String STYLE_HELP_COMMAND = "-fx-text-fill: #e51c23; -fx-padding: 0 0 0 50; -fx-font-size: 24";
	
	private static final String STYLE_FLOAT_DIVISION = "-fx-background-color: white";
	private static final String STYLE_CAPTION = "-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;";
	
	private static final double SIZE_HEIGHT_HELPBOX = 40;
	private static final double SIZE_WIDTH_HELPBOX = 900;
	private static final double SIZE_HEIGHT_HELP_GRIDPANE = 140;
	private static final double SIZE_WIDTH_HELP_GRIDPANE = 900;
	private static final double SIZE_HEIGHT_FLOAT_DIVISION = 20;
	private static final double SIZE_WIDTH_FLOAT_DIVISION = 850;
	
	private static final double SPACING_HELP_COOMPONENT = 20;
	
	private static final String CLASS_NAME_TASKCARD = "taskCard";
	
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
	
	private FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_TIME));
	
	// Pages
	private ScrollPane[] scrollPage;
	private VBox[] page;
	private GridPane[] commandInfo;
	private Label[] fKeys;
	private Label[] fKeysInfo;
	
	String command;
	String feedback;
	int pageCount;
	TaskList taskList;
	
	String searchKey;
	String showTag;
	String showPeriod;
	List<Date> periodDate;
	
	private List<String> historyCommands;
	private int historyPointer;
	
	private Hashtable<String, String> tagColor;
	private int colorPointer;
	
	final SimpleDateFormat taskTimeFormat = new SimpleDateFormat(FORMAT_TASK_TIME, Locale.US);
	final SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE, Locale.US);
	final SimpleDateFormat dateFormatForShow = new SimpleDateFormat(FORMAT_DATE_FOR_SHOW, Locale.US);
	final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			final Calendar cal = Calendar.getInstance();
			date.setText(dateFormat.format(cal.getTime()));
		}
	}));
	
	//@author A0119414L
	/**
	 * Constructor of MainViewController.
	 * 
	 * @throws IOException
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	public MainViewController() throws IOException, TaskInvalidDateException, TaskNoSuchTagException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        fxmlLoader.load();
        
        setMainView();
	}
	
	//@author A0119414L
	/**
	 * Initialize the view.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void setMainView() throws TaskInvalidDateException, TaskNoSuchTagException {
		initFadeEffect();
		setPageCount(TOTAL_PAGE_NUM);
		setPages();
		setFont();
		initTagColor();
        setDate();
        initMainDisplay();
        setRestTaskResponse();
        setDisplayTitleText();
        initHistory();
        initTextFieldKey();
	}
	
	//@author A0119414L
	/**
	 * Initialize the history command and history command pointer.
	 * 
	 */
	private void initHistory() {
		historyCommands = new ArrayList<String>();
		historyPointer = INITIAL_POSITION;
	}
	
	//@author A0119414L
	/**
	 * Initialize the function up and down keys 
	 * 
	 */
	private void initTextFieldKey() {
		setUpKey();
		setDownKey();
	}
	
	//@author A0119414L
	/**
	 * Set the title of current page.
	 * Pages includes today tasks, period tasks, undone tasks, done tasks,
	 * overdue tasks, tasks with tag, search result, and help document.
	 * 
	 */
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
	
	//@author A0119414L
	/**
	 * Initialize the fade out effect.
	 * 
	 */
	private void initFadeEffect() {
		fadeOut.setNode(response);
		fadeOut.setFromValue(FADE_FROM_VALUE);
		fadeOut.setToValue(FADE_TO_VALUE);
		fadeOut.setCycleCount(FADE_CYCLES);
		fadeOut.setAutoReverse(FADE_AUTO_REVERSE);
	}
	
	//@author A0119414L
	/**
	 * Set the total number of pages.
	 * 
	 * @param count
	 */
	private void setPageCount(int count) {
		pageCount = count;
	}
	
	//@author A0119414L
	/**
	 * Initialize each page.
	 * 
	 */
	private void setPages() {
		
		scrollPage = new ScrollPane[pageCount];
		page = new VBox[pageCount];
		for (int i=0; i<pageCount; i++) {
			scrollPage[i] = new ScrollPane();
			setScrollPage(i);
			page[i] = new VBox();
			page[i].setPrefHeight(listDisplay.getPrefHeight() - DIFFERENCE_HEIGHT);
			page[i].setPrefWidth(listDisplay.getPrefWidth());
			page[i].setStyle(STYLE_PAGE);
			scrollPage[i].setContent(page[i]);
		}
		
		setLeftKey();
		setRightKey();
		setEscKey();
		for (int i=0; i<TOTAL_FKEYS; i++) {
			setFKey(i);
		}
		setPageUpKey();
		setPageDownKey();
		
	}
	
	//@author A0119414L
	/**
	 * Set the size and style of each page.
	 * 
	 * @param index		Page index.
	 */
	private void setScrollPage(int index) {
		scrollPage[index].setStyle(CSS_BACKGROUND_COLOR + String.format(FX_COLOR_RGB, 255, 255, 255));
		scrollPage[index].setPrefSize(listDisplay.getPrefWidth(), listDisplay.getPrefHeight());
		scrollPage[index].setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPage[index].setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPage[index].setFitToWidth(true);
		scrollPage[index].getStyleClass().add("taskListPane");
	}
	
	//@author A0119414L
	/**
	 * Set font size of some components.
	 * 
	 */
	private void setFont() {
	    date.setStyle(STYLE_FONT_DATE);
	    response.setStyle(STYLE_FONT_RESPONSE);
	    input.setStyle(STYLE_FONT_INPUT);
	    displayTitleText.setStyle(STYLE_FONT_DISPLAYTITLETEXT);
	}
	
	//@author A0119414L
	/**
	 * Initialize tag color and tag color pointer.
	 * 
	 */
	private void initTagColor() {
		tagColor = new Hashtable<String, String>();
		colorPointer = INITIAL_POSITION;
	}
	
	//@author A0119414L
	/**
	 * Display the date to the view.
	 * 
	 */
	private void setDate() {
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	//@author A0119414L
	/**
	 * Implement the page content to each page.
	 * 
	 * @throws TaskInvalidDateException
	 */
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
	
	//@author A0119414L
	/**
	 * Set the home page of the help document page.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void setHelpHomePage() throws TaskInvalidDateException, TaskNoSuchTagException {
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		setRestTaskResponse();
		
		commandInfo = new GridPane[TOTAL_FKEYS];
		fKeys = new Label[TOTAL_FKEYS];
		fKeysInfo = new Label[TOTAL_FKEYS];
		
		for (int i=0; i<TOTAL_FKEYS-1; i++) {
			
			commandInfo[i] = new GridPane();
			setHelpHBoxSize(i, SIZE_WIDTH_HELPBOX, SIZE_HEIGHT_HELPBOX);
			
			fKeys[i] = new Label(F_KEYS[i].toString());
			fKeys[i].setStyle(STYLE_F_KEYS);
			
			fKeysInfo[i] = new Label(F_KEY_COMMAND[i].toString());
			fKeysInfo[i].setStyle(STYLE_F_KEYS_INFO);
			
			GridPane.setConstraints(fKeys[i], 0, 0);
			commandInfo[i].getChildren().add(0, fKeys[i]);
			GridPane.setConstraints(fKeysInfo[i], 1, 0);
			commandInfo[i].getChildren().add(1, fKeysInfo[i]);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[i]);
			
			GridPane.setMargin(fKeys[i], new Insets(5, 15, 5, 0));
			GridPane.setMargin(fKeysInfo[i], new Insets(5, 15, 5, 16));
			
		}
		commandInfo[F_KEY_OTHER_COMMAND] = new GridPane();
		setHelpHBoxSize(F_KEY_OTHER_COMMAND, SIZE_WIDTH_HELPBOX, SIZE_HEIGHT_HELPBOX);
		
		fKeys[F_KEY_OTHER_COMMAND] = new Label(F_KEYS[F_KEY_OTHER_COMMAND].toString());
		fKeys[F_KEY_OTHER_COMMAND].setStyle(STYLE_F_KEYS);
		
		fKeysInfo[F_KEY_OTHER_COMMAND] = new Label(DISPLAY_HELP_OTHER_COMMAND);
		fKeysInfo[F_KEY_OTHER_COMMAND].setStyle(STYLE_F_KEYS_INFO);
		
		GridPane.setConstraints(fKeys[F_KEY_OTHER_COMMAND], 0, 0);
		commandInfo[F_KEY_OTHER_COMMAND].getChildren().add(fKeys[F_KEY_OTHER_COMMAND]);
		GridPane.setConstraints(fKeysInfo[F_KEY_OTHER_COMMAND], 1, 0);
		commandInfo[F_KEY_OTHER_COMMAND].getChildren().add(fKeysInfo[F_KEY_OTHER_COMMAND]);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().add(commandInfo[F_KEY_OTHER_COMMAND]);
		
		GridPane.setMargin(fKeys[F_KEY_OTHER_COMMAND], new Insets(5, 15, 5, 50));
		GridPane.setMargin(fKeysInfo[F_KEY_OTHER_COMMAND], new Insets(5, 15, 5, 16));
	}
	
	//@author A0119414L
	/**
	 * Set the size of each help box (each column) in the view.
	 * 
	 * @param index		Column index.
	 * @param width		Width to set.
	 * @param height	Height to set.
	 */
	private void setHelpHBoxSize(int index, double width, double height) {
		commandInfo[index].setPrefSize(width, height);
		commandInfo[index].setMaxSize(width, height);
		commandInfo[index].setMinSize(width, height);
	}
	
	//@author A0119414L
	/**
	 * Set the size and style of each command in help page.
	 * 
	 * @param command				Command.
	 * @param descriptionString		Description of a command.
	 * @param structureString		Structure of a command.
	 * @param exampleString			Example of a command.
	 */
	private void setHelpForCommand(GridPane command, String descriptionString, String structureString, String exampleString) {
		Label description = new Label(DISPLAY_HELP_DISCRIPTION);
		description.setStyle(STYLE_HELP_DESCRIPTION);
		Label structure = new Label(DISPLAY_HELP_STRUCTURE);
		structure.setStyle(STYLE_HELP_STRUCTURE);
		Label example = new Label(DISPLAY_HELP_EXAMPLE);
		example.setStyle(STYLE_HELP_EXAMPLE);
		
		command.setStyle(STYLE_HELP_GRIDPANE);
		setGridPaneSize(command, SIZE_WIDTH_HELP_GRIDPANE, SIZE_HEIGHT_HELP_GRIDPANE);
		Label descriptionContent = new Label(descriptionString);
		descriptionContent.setStyle(STYLE_HELP_DESCRIPTION_CONTENT);
		Label structureContent = new Label(structureString);
		structureContent.setStyle(STYLE_HELP_STRUCTURE_CONTENT);
		Label exampleContent = new Label(exampleString);
		exampleContent.setStyle(STYLE_HELP_EXAMPLE_CONTENT);
		
		GridPane.setConstraints(description, 0, 0);
		GridPane.setConstraints(descriptionContent, 0, 1);
		GridPane.setConstraints(structure, 0, 2);
		GridPane.setConstraints(structureContent, 0, 3);
		GridPane.setConstraints(example, 0, 4);
		GridPane.setConstraints(exampleContent, 0, 5);
		command.getChildren().addAll(description, descriptionContent, structure, structureContent, example, exampleContent);
	}

	//@author A0119414L
	/**
	 * Put all the help information for all commands into the help page.
	 * 
	 * @param index		The page index of each type of command to display.
	 */
	private void setHelpPage(int index) {
		response.setText(TITLE_HELP_COMMAND);
		page[HELP_DOC_PAGE_INDEX].setSpacing(SPACING_HELP_COOMPONENT);
		if (index == F_KEY_ADD) {
			
			Label add = new Label(DISPLAY_HELP_COMMAND[F_KEY_ADD]);
			add.setStyle(STYLE_HELP_COMMAND);
			
			GridPane addFloatTask = new GridPane();
			setHelpForCommand(addFloatTask, DISPLAY_HELP_ADD_FLOATING_TASK_DESCRIPTION, 
					DISPLAY_HELP_ADD_FLOATING_TASK_STRUCTURE, DISPLAY_HELP_ADD_FLOATING_TASK_EXAMPLE);
			
			GridPane addDeadlineTask = new GridPane();
			setHelpForCommand(addDeadlineTask, DISPLAY_HELP_ADD_DEADLINE_TASK_DESCRIPTION, 
					DISPLAY_HELP_ADD_DEADLINE_TASK_STRUCTURE, DISPLAY_HELP_ADD_DEADLINE_TASK_EXAMPLE);
			
			GridPane addRepeatedTask = new GridPane();
			setHelpForCommand(addRepeatedTask, DISPLAY_HELP_ADD_REPEATED_TASK_DESCRIPTION, 
					DISPLAY_HELP_ADD_REPEATED_TASK_STRUCTURE, DISPLAY_HELP_ADD_REPEATED_TASK_EXAMPLE);
			
			GridPane addFixedTask = new GridPane();
			setHelpForCommand(addFixedTask, DISPLAY_HELP_ADD_FIXED_TASK_DESCRIPTION, 
					DISPLAY_HELP_ADD_FIXED_TASK_STRUCTURE, DISPLAY_HELP_ADD_FIXED_TASK_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(add, addFloatTask, addDeadlineTask, addRepeatedTask, addFixedTask);
			
		} else if (index == F_KEY_DELETE) {
			
			Label delete = new Label(DISPLAY_HELP_COMMAND[F_KEY_DELETE]);
			delete.setStyle(STYLE_HELP_COMMAND);
			
			GridPane deleteOneTask = new GridPane();
			setHelpForCommand(deleteOneTask, DISPLAY_HELP_DELETE_ONE_TASK_DESCRIPTION, 
					DISPLAY_HELP_DELETE_ONE_TASK_STRUCTURE, DISPLAY_HELP_DELETE_ONE_TASK_EXAMPLE);
			
			GridPane deleteManyTasks = new GridPane();
			setHelpForCommand(deleteManyTasks, DISPLAY_HELP_DELETE_MANY_TASKS_DESCRIPTION, 
					DISPLAY_HELP_DELETE_MANY_TASKS_STRUCTURE, DISPLAY_HELP_DELETE_MANY_TASKS_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(delete, deleteOneTask, deleteManyTasks);
			
		} else if (index == F_KEY_DONE) {
			
			Label done = new Label(DISPLAY_HELP_COMMAND[F_KEY_DONE]);
			done.setStyle(STYLE_HELP_COMMAND);
			
			GridPane doneOneTask = new GridPane();
			setHelpForCommand(doneOneTask, DISPLAY_HELP_DONE_ONE_TASK_DESCRIPTION, 
					DISPLAY_HELP_DONE_ONE_TASK_STRUCTURE, DISPLAY_HELP_DONE_ONE_TASK_EXAMPLE);
			
			GridPane doneManyTasks = new GridPane();
			setHelpForCommand(doneManyTasks, DISPLAY_HELP_DONE_MANY_TASKS_DESCRIPTION, 
					DISPLAY_HELP_DONE_MANY_TASKS_STRUCTURE, DISPLAY_HELP_DONE_MANY_TASKS_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(done, doneOneTask, doneManyTasks);
			
		} else if (index == F_KEY_EDIT) {
			
			Label edit = new Label(DISPLAY_HELP_COMMAND[F_KEY_EDIT]);
			edit.setStyle(STYLE_HELP_COMMAND);
			
			GridPane editTaskDescription = new GridPane();
			setHelpForCommand(editTaskDescription, DISPLAY_HELP_EDIT_DESCRIPTION_DESCRIPTION, 
					DISPLAY_HELP_EDIT_DESCRIPTION_STRUCTURE, DISPLAY_HELP_EDIT_DESCRIPTION_EXAMPLE);
			
			GridPane editTaskTime = new GridPane();
			setHelpForCommand(editTaskTime, DISPLAY_HELP_EDIT_TIME_DESCRIPTION, 
					DISPLAY_HELP_EDIT_TIME_STRUCTURE, DISPLAY_HELP_EDIT_TIME_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(edit, editTaskDescription, editTaskTime);
			
		} else if (index == F_KEY_SEARCH) {
			
			Label search = new Label(DISPLAY_HELP_COMMAND[F_KEY_SEARCH]);
			search.setStyle(STYLE_HELP_COMMAND);
			
			GridPane searchKeyword = new GridPane();
			setHelpForCommand(searchKeyword, DISPLAY_HELP_SEARCH_DESCRIPTION, 
					DISPLAY_HELP_SEARCH_STRUCTURE, DISPLAY_HELP_SEARCH_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(search, searchKeyword);
			
		} else if (index == F_KEY_SHOW) {
			
			Label show = new Label(DISPLAY_HELP_COMMAND[F_KEY_SHOW]);
			show.setStyle(STYLE_HELP_COMMAND);
			
			GridPane showAll = new GridPane();
			setHelpForCommand(showAll, DISPLAY_HELP_SHOW_ALL_DESCRIPTION, 
					DISPLAY_HELP_SHOW_ALL_STRUCTURE, DISPLAY_HELP_SHOW_ALL_EXAMPLE);
			
			GridPane showDone = new GridPane();
			setHelpForCommand(showDone, DISPLAY_HELP_SHOW_DONE_DESCRIPTION, 
					DISPLAY_HELP_SHOW_DONE_STRUCTURE, DISPLAY_HELP_SHOW_DONE_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(show, showAll, showDone);
			
		} else if (index == F_KEY_TAG) {
			
			Label tag = new Label(DISPLAY_HELP_COMMAND[F_KEY_TAG]);
			tag.setStyle(STYLE_HELP_COMMAND);
			
			GridPane tagOneTask = new GridPane();
			setHelpForCommand(tagOneTask, DISPLAY_HELP_TAG_ONE_DESCRIPTION, 
					DISPLAY_HELP_TAG_ONE_STRUCTURE, DISPLAY_HELP_TAG_ONE_EXAMPLE);
			
			GridPane untagOneTag = new GridPane();
			setHelpForCommand(untagOneTag, DISPLAY_HELP_UNTAG_ONE_DESCRIPTION, 
					DISPLAY_HELP_UNTAG_ONE_STRUCTURE, DISPLAY_HELP_UNTAG_ONE_EXAMPLE);
			
			GridPane untagAllTags = new GridPane();
			setHelpForCommand(untagAllTags, DISPLAY_HELP_UNTAG_ALL_DESCRIPTION, 
					DISPLAY_HELP_UNTAG_ALL_STRUCTURE, DISPLAY_HELP_UNTAG_ALL_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(tag, tagOneTask, untagOneTag, untagAllTags);
			
		} else {
			
			Label otherCommands = new Label(DISPLAY_HELP_OTHER_COMMAND);
			otherCommands.setStyle(STYLE_HELP_COMMAND);
			
			GridPane clear = new GridPane();
			setHelpForCommand(clear, DISPLAY_HELP_CLEAR_DESCRIPTION, 
					DISPLAY_HELP_CLEAR_STRUCTURE, DISPLAY_HELP_CLEAR_EXAMPLE);
			
			GridPane exit = new GridPane();
			setHelpForCommand(exit, DISPLAY_HELP_EXIT_DESCRIPTION, 
					DISPLAY_HELP_EXIT_STRUCTURE, DISPLAY_HELP_EXIT_EXAMPLE);
			
			page[HELP_DOC_PAGE_INDEX].getChildren().clear();
			page[HELP_DOC_PAGE_INDEX].getChildren().addAll(otherCommands, clear, exit);
			
		}
	}
	
	//@author A0119414L
	/**
	 * Display today tasks.
	 * 
	 * @throws TaskInvalidDateException
	 */
	private void displayTodayTasks() throws TaskInvalidDateException {
		setOnePageView(TODAY_TASKS_PAGE_INDEX, getTodayTaskList());
	}
	
	//@author A0119414L
	/**
	 * Display tasks during a period of time.
	 * 
	 * @throws TaskInvalidDateException
	 */
	private void displayPeriodTasks() throws TaskInvalidDateException {
		setOnePageView(PERIOD_TASKS_PAGE_INDEX, getPeriodTaskList(showPeriod));
	}
	
	//@author A0119414L
	/**
	 * Get the list of tasks that are within a period of time.
	 * 
	 * @param userCommand		Input command from user.
	 * @return		List of tasks that within the time period specified in user command.
	 * @throws TaskInvalidDateException
	 */
	private List<Task> getPeriodTaskList(String userCommand) throws TaskInvalidDateException {
		List<Date> periodDate = Logic.getDateList(userCommand);
		this.periodDate = periodDate;
		List<Task> periodTasks = taskList.getDateRangeTask(periodDate);
		
		return periodTasks;
	}
	
	//@author A0119414L
	/**
	 * Get the taskList from Logic and check overdue for each task.
	 * 
	 * @throws TaskInvalidDateException
	 */
	private void loadTaskListToController() throws TaskInvalidDateException {
		loadTaskList();
		taskList = getTaskList();
		taskList.checkOverdue();
	}
	
	//@author A0119414L
	/**
	 * Set view of one page (this method is only for the page that is to show all ongoing tasks).
	 * 
	 * @param pageIndex		Page index.
	 * @throws TaskInvalidDateException
	 */
	private void setOnePageView(int pageIndex) throws TaskInvalidDateException {
		page[pageIndex].getChildren().clear();
		
		for (int i=INITIAL_POSITION; i<taskList.countUndone(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.getStyleClass().add(CLASS_NAME_TASKCARD);
      
			setTaskFormat(taskLayout, i);F
			
			if (((taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false)) == -1) 
					|| (taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false)) > 0)) && (i==INITIAL_POSITION)) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle(STYLE_FLOAT_DIVISION);
				Label caption = new Label();
				caption.setText(DISPLAY_TITLE_TASK_DUE_SOON);
				caption.setStyle(STYLE_CAPTION);
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, SIZE_WIDTH_FLOAT_DIVISION, SIZE_HEIGHT_FLOAT_DIVISION);
				page[pageIndex].getChildren().add(floatDivision);
			} else if ((taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false)) == 0) && (i==0)) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle(STYLE_FLOAT_DIVISION);
				Label caption = new Label();
				caption.setText(DISPLAY_TITLE_TASK_TO_DO);
				caption.setStyle(STYLE_CAPTION);
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, SIZE_WIDTH_FLOAT_DIVISION, SIZE_HEIGHT_FLOAT_DIVISION);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].setSpacing(10);
			page[pageIndex].getChildren().add(taskLayout);
			
			if ((taskList.indexOfFirstFloatingTask(taskList.prepareDisplayList(false))-1 == i)) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) To Do");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
		}
		page[pageIndex].getChildren().add(new Pane());
	}
	
	//@author A0119414L
	/**
	 * Set view of one page (except help document page and all ongoing tasks page).
	 * 
	 * @param pageIndex		Page index.
	 * @param specificTaskList		Specified task list to display in the page.
	 * @throws TaskInvalidDateException
	 */
	private void setOnePageView(int pageIndex, List<Task> specificTaskList) throws TaskInvalidDateException {
		listDisplay.setCurrentPageIndex(pageIndex);
		page[pageIndex].getChildren().clear();
		
		for (int i=0; i<specificTaskList.size(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.getStyleClass().add("taskCard");
            
			setTaskFormat(taskLayout, specificTaskList.get(i), i);
			
			if ((((taskList.indexOfFirstFloatingTask(specificTaskList) == -1) 
					|| (taskList.indexOfFirstFloatingTask(specificTaskList) > 0)) && (i==0)) && pageIndex!=DONE_TASKS_PAGE_INDEX && pageIndex!=SEARCH_RESULT_PAGE_INDEX) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) Due Soon");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			} else if (((taskList.indexOfFirstFloatingTask(specificTaskList) == 0) && (i==0)) && pageIndex!=DONE_TASKS_PAGE_INDEX && pageIndex!=SEARCH_RESULT_PAGE_INDEX) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) To Do");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
			
			page[pageIndex].setSpacing(10);
			page[pageIndex].getChildren().add(taskLayout);
			
			if (((taskList.indexOfFirstFloatingTask(specificTaskList)-1 == i)) && pageIndex!=DONE_TASKS_PAGE_INDEX && pageIndex!=SEARCH_RESULT_PAGE_INDEX) {
				GridPane floatDivision = new GridPane();
				floatDivision.setStyle("-fx-background-color: white");
				Label caption = new Label();
				caption.setText("Task(s) To Do");
				caption.setStyle("-fx-text-fill: #9E9E9E;-fx-font-weight: bold;-fx-font-size: 16px;");
				GridPane.setConstraints(caption, 0, 0);
				floatDivision.getChildren().add(caption);
				setGridPaneSize(floatDivision, 850, 20);
				page[pageIndex].getChildren().add(floatDivision);
			}
		}
		page[pageIndex].getChildren().add(new Pane());
	}
	
	//@author A0119414L
	/**
	 * Set size and style for each task (each column in the view).
	 * Different task may be displayed in different size and style.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param task			Task to display, including all the task information. 
	 * @param index			Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
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
	
	//@author A0119414L
	/**
	 * Set size and style for each task (each column in the view).
	 * Different task may be displayed in different size and style. 
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param index				Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
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
	
	//@author A0119414L
	/**
	 * Set the task view if it is a floating task.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param task			Task to display, including all the task information. 
	 * @param index			Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
	private void setFloatTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, false, task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	//@author A0119414L
	/**
	 * Set the task view if it is a deadline task.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param task			Task to display, including all the task information. 
	 * @param index			Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
	private void setDeadlineTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	//@author A0119414L
	/**
	 * Set the task view if it is a fixed task.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param task			Task to display, including all the task information. 
	 * @param index			Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
	private void setFixedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setStartTime(taskLayout, task);
		setDeadline(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	//@author A0119414L
	/**
	 * Set the task view if it is a repeated task.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param task			Task to display, including all the task information. 
	 * @param index			Index of the task in the task list.
	 * @throws TaskInvalidDateException
	 */
	private void setRepeatedTaskFormat(GridPane taskLayout, Task task, int index) throws TaskInvalidDateException {
		setDisplayIndex(taskLayout, index, task.getIsOverdue(), task.getIsDone());
		setSpace(taskLayout);
		setDescription(taskLayout, task);
		setStatus(taskLayout, task);
		setDeadline(taskLayout, task);
		setRepeatPeriod(taskLayout, task);
		setTags(taskLayout, task);
	}
	
	//@author A0119414L
	/**
	 * Set the style of task index.
	 * 
	 * @param taskLayout	GridPane to hold the content of each task.
	 * @param index			Index of the task in the task list.
	 * @param isOverdue		It is true if the task is overdue.
	 * @param isDone		It is true if the task is done.
	 */
	private void setDisplayIndex(GridPane taskLayout, int index, boolean isOverdue, boolean isDone) {
		Label displayIndex = new Label(Integer.toString(index+1));
	
		displayIndex.setMaxHeight(150);
		displayIndex.setMinWidth(50);
		displayIndex.setMaxWidth(50);
		 if (isDone) {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#4CAF50;-fx-background-radius: 2px 0 0 2px;");
		} else if (isOverdue) {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#F44336;-fx-background-radius: 2px 0 0 2px;");
		} else {
			displayIndex.setStyle("-fx-text-fill: white; -fx-alignment: center; -fx-font-size: 30; -fx-background-color:#29b6f6;-fx-background-radius: 2px 0 0 2px;");
		}
		
		GridPane.setConstraints(displayIndex, 0, 0, 1, 5);
		taskLayout.getChildren().add(displayIndex);
	}
	
	//@author A0119414L
	/**
	 * Set the white space in task layout.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 */
	private void setSpace(GridPane taskLayout) {
		Label space = new Label("  ");
		GridPane.setConstraints(space, 2, 0, 1, 1);
		taskLayout.getChildren().add(space);
	}
	
	//@author A0119414L
	/**
	 * Set the style of task description.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information. 
	 */
	private void setDescription(GridPane taskLayout, Task task) {
		Label description = new Label(task.getDescription());
		description.setPrefSize(650, 40);
		description.setMaxHeight(40);
		description.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		description.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px ; -fx-font-size:28;");
		GridPane.setConstraints(description, 1, 0, 1, 1);
		taskLayout.getChildren().add(description);
	}
	
	//@author A0119414L
	/**
	 * Set the style of the status.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information. 
	 * @throws TaskInvalidDateException
	 */
	private void setStatus(GridPane taskLayout, Task task) {
		Label status = new Label();
		status.setPrefWidth(150);
		status.setMaxHeight(140);
		status.setMinWidth(100);
		status.setMaxWidth(150);
		status.setAlignment(Pos.CENTER);
		status.getStyleClass().add("status-card");
		
		determineStatusText(status, task);
		
		GridPane.setConstraints(status, 2, 0, 1, 5);
		taskLayout.getChildren().add(status);
	}
	
	//@author A0119446B
	/**
	 * Set the proper text to display for Status label
	 * 
	 * @param status		The label for displaying
	 * @param task			Task to display, including all the task information. 
	 * @throws TaskInvalidDateException
	 */
	private void determineStatusText(Label status, Task task) {
		if (task.getType().equals(Type.FLOAT)) {
			if (task.getIsDone()) {
				status.setText("DONE");
				status.setStyle("-fx-background-color: #4CAF50;-fx-background-radius: 0 2px 2px 0;");
			} else {
				status.setText("ONGOING");
				status.setStyle("-fx-background-color: #29b6f6;-fx-background-radius: 0 2px 2px 0;");
			}
		} else {
			try {
				task.checkOverdue();
			} catch (TaskInvalidDateException e) {
				
			}
			int remainDays = task.getReminingDays();
			if (task.getIsDone()) {
				status.setText("DONE");
				status.setStyle("-fx-background-color: #4CAF50;-fx-background-radius: 0 2px 2px 0;");
			} else {
				if (task.getIsOverdue()) {
					status.setText("OVERDUE");
					status.setStyle("-fx-background-color: #F44336;-fx-background-radius: 0 2px 2px 0;");
				} else {
					if (remainDays == 0) {
						status.setText("Due Today");
					} else if (remainDays == 1) {
						status.setText("1 Day Left");
					} else {
						status.setText(remainDays + " Days Left");
					}
					status.setStyle("-fx-background-color: #29b6f6;-fx-background-radius: 0 2px 2px 0;");
				}
			}
		}
	}

	//@author A0119414L
	/**
	 * Set the style of deadline if it is not floating task.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information. 
	 * @throws TaskInvalidDateException
	 */
	private void setDeadline(GridPane taskLayout, Task task) throws TaskInvalidDateException {
		Label deadline = new Label();
		
		deadline.setText("Deadline:   " + taskTimeFormat.format(task.getDeadline()));
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
	
	//@author A0119414L
	/**
	 * Set the style of start time if it is fixed task.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information. 
	 * @throws TaskInvalidDateException
	 */
	private void setStartTime(GridPane taskLayout, Task task) {
		FixedTask fixedTask = (FixedTask)task;
		Label startTime = new Label("Start Time: " + taskTimeFormat.format(fixedTask.getStartTime()));
		
		startTime.setPrefSize(650, 30);
		startTime.setMaxHeight(30);
		startTime.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px; -fx-font-size:16;");
		
		GridPane.setConstraints(startTime, 1, 1, 3, 1);
		taskLayout.getChildren().add(startTime);
	}
	
	//@author A0119414L
	/**
	 * Set the style of repeated period if it is repeated task.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information. 
	 */
	private void setRepeatPeriod(GridPane taskLayout, Task task) {
		RepeatedTask repeatedTask = (RepeatedTask)task;
		Label repeatPeriod = new Label("Repeat Period: " + repeatedTask.getRepeatPeriod());
		repeatPeriod.setStyle("-fx-text-fill: rgb(0,0,0,87); -fx-padding:0 0 0 16px; -fx-font-size:16;");
		repeatPeriod.setPrefSize(650, 30);
		repeatPeriod.setMaxHeight(30);
		GridPane.setConstraints(repeatPeriod, 1, 2, 3, 1);
		taskLayout.getChildren().add(repeatPeriod);
	}
	
	//@author A0119414L
	/**
	 * Set the style of tags if the task has any tags.
	 * 
	 * @param taskLayout		GridPane to hold the content of each task.
	 * @param task				Task to display, including all the task information.
	 */
	private void setTags(GridPane taskLayout, Task task) {
		Type taskType = task.getType();
		HBox tagBox = new HBox();
		tagBox.setStyle("-fx-padding: 0 16px 0 16px; -fx-valignment: center;-fx-border-width: 1 0 0 0;-fx-border-color: #E0E0E0");
		tagBox.setAlignment(Pos.CENTER_LEFT);
		tagBox.setSpacing(20);
		tagBox.setPrefSize(650, 30);
		tagBox.setMaxWidth(650);
		tagBox.setMaxHeight(30);
		if (task.getTags().size() > 0) {
			Label[] tags = new Label[task.getTags().size()];
			
			for (int j=0; j<task.getTags().size(); j++) {
				List<String> tagList = task.getTags();
				tags[j] = new Label(tagList.get(j));
				tags[j].setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
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
	
	//@author A0119414L
	/**
	 * Set size of a GridPane.
	 * 
	 * @param gridPane		GridPane to set size.
	 * @param width			Width to set.
	 * @param height		Height to set.
	 */
	private void setGridPaneSize(GridPane gridPane, double width, double height) {
		gridPane.setPrefSize(width, height);
		gridPane.setMaxSize(width, height);
		gridPane.setMinSize(width, height);
	}	
	
	//@author A0119414L
	/**
	 * Set the dynamic response due to the task list and different page.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
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
				response.setText("No time period shown yet. Try \"show this week\".");
			} else {
				List<Task> periodTasks = getPeriodTaskList(showPeriod);
				if (periodTasks.size() > 1) {
					response.setText(String.format(TITLE_SHOW_TASKS_IN_PERIOD, 
							dateFormatForShow.format(periodDate.get(0)), dateFormatForShow.format(periodDate.get(1))));
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
				response.setText("You haven't show tags yet. Try \"show <tags>\".");
			} else {
				if (taskList.isTagContained(showTag)) {
					List<Task> tagTasks = getTagTaskList(showTag);
					if (tagTasks.size() > 1) {
						response.setText(String.format(TITLE_SHOW_TASKS_WITH_TAG, showTag));
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
				response.setText("You haven't search anything yet. Try \"search <keyword>\".");
			} else {
				int count = taskList.searchTaskByKeyword(searchKey).size();
				if (count == 0) {
					response.setText(String.format("Nothing found for %s", searchKey));
				} else if (count == 1) {
					response.setText(String.format(TITLE_SHOW_SEARCH_RESULT, searchKey));
				} else {
					response.setText(String.format(TITLE_SHOW_SEARCH_RESULT, searchKey));
				}
			}
		} else if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX){
			response.setText("Press \"F1\" - \"F8\" for more details.");
		} else {
			
		}
	}
	
	//@author A0119414L
	/**
	 * Get the user input command from the view.
	 * 
	 * @return		return the command.
	 */
	private String getUserInput() {
		return input.getText();
	}
	
	//@author A0119414L
	/**
	 * Call Logic to execute the user command.
	 * 
	 * @param command	Command to execute.
	 * @return			return the feedback of executing the command.
	 */
	private String executeCommand(String command) {
		return Logic.readAndExecuteCommands(command);
	}
	
	//@author A0119414L
	/**
	 * Check if the command is some special command.
	 * Special commands are the commands different from executive commands.
	 * 
	 * @return		return true if it is a special command.
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException 
	 */
	private boolean isSpecialCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (command.length() < 4) {
			return false;
		}
		
		if (command.trim().toLowerCase().equals("exit")) {
			// close logger
			ULogger.close();
			
			saveTaskList();
			Platform.exit();
		}
		
		if (command.trim().toLowerCase().substring(0, 4).equals("find") 
				|| command.trim().toLowerCase().substring(0, 4).equals("goto")) {
			String stringIndex = command.trim().toLowerCase().substring(5);
			int indexOfTask = 1;
			try {
				indexOfTask = Integer.parseInt(stringIndex);
			} catch (NumberFormatException e) {
				
			}
			
			if (listDisplay.getCurrentPageIndex() == TODAY_TASKS_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(getTodayTaskList(), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == PERIOD_TASKS_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(getPeriodTaskList(showPeriod), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == UNDONE_TASKS_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(taskList.prepareDisplayList(false), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == DONE_TASKS_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(taskList.getFinishedTasks(), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == OVERDUE_TASKS_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(taskList.getOverdueTask(), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == TASKS_WITH_TAG_PAGE_INDEX) {
				moveToSpecificPosition(findIndexPosition(taskList.getTasksWithTag(showTag), indexOfTask));
			} else if (listDisplay.getCurrentPageIndex() == SEARCH_RESULT_PAGE_INDEX) {
				if (searchKey != null) {
					moveToSpecificPosition(findIndexPosition(taskList.searchTaskByKeyword(searchKey), indexOfTask));
				}
			}
			setTextFieldEmpty();
			return true;
		}
		
		if (command.trim().toLowerCase().substring(0, 4).equals("demo")) {
			Logic.readAndExecuteCommands("clear");
			Logic.readAndExecuteCommands("add project meeting by 7pm");
			Logic.readAndExecuteCommands("add family dinner by sat 6.30pm");
			Logic.readAndExecuteCommands("add Telepaste IPO by tomorrow");
			Logic.readAndExecuteCommands("add visit the zoo by nov 27");
			Logic.readAndExecuteCommands("add reading Harry Potter");
			Logic.readAndExecuteCommands("add find a girl/boy friend");
			Logic.readAndExecuteCommands("tag 1 !!!");
			Logic.readAndExecuteCommands("tag 2 family");
			Logic.readAndExecuteCommands("tag 3 !!");
			Logic.readAndExecuteCommands("tag 5 book");
			Logic.readAndExecuteCommands("tag 6 ><");
			Logic.readAndExecuteCommands("add Broing meeting by yesterday");
			Logic.readAndExecuteCommands("add OP2");
			Logic.readAndExecuteCommands("done 8");
			
			taskList = getTaskList();
			taskList.checkOverdue();
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
			setDisplayTitleText();
			setRestTaskResponse();
			setTextFieldEmpty();
			
			return true;
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
			setTextFieldEmpty();
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
			setTextFieldEmpty();
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
	
	//@author A0119414L
	/**
	 * Call Logic to save the task list to the Storage.
	 * 
	 */
	private void saveTaskList() {
		Logic.saveTaskList();
	}
	
	//@author A0119414L
	/**
	 * Call Logic to load the task list from the Storage to Logic.
	 * 
	 */
	private void loadTaskList() {
		Logic.loadTaskList();
	}
	
	//@author A0119414L
	/**
	 * Get the task list from Logic.
	 * 
	 * @return		return the task list from Logic.
	 */
	private TaskList getTaskList() {
		return Logic.getTaskList();
	}
	
	//@author A0119414L
	/**
	 * Get the list of tasks which deadline is today from Logic.
	 * 
	 * @return		return the list of tasks due today.
	 * @throws TaskInvalidDateException
	 */
	private List<Task> getTodayTaskList() throws TaskInvalidDateException {
		List<Date> today = Logic.getDateList("show today");
		List<Task> todayTasks = taskList.getDateRangeTask(today);
		
		return todayTasks;
	}
	
	//@author A0119414L
	/**
	 * Get the list of tasks which are with the specified tag.
	 * 
	 * @param tag		Tag that tasks have.
	 * @return			return the list of tasks with such tag.
	 * @throws TaskNoSuchTagException
	 */
	private List<Task> getTagTaskList(String tag) throws TaskNoSuchTagException {
		List<Task> tagTasks = taskList.getTasksWithTag(tag);
		
		return tagTasks;
	}
	
	//@author A0119414L
	/**
	 * Set the text of input field in the view.
	 * 
	 * @param content		Text to set.
	 */
	private void setTextField(String content) {
		input.setText(content);
	}
	
	//@author A0119414L
	/**
	 * Set the input field to be empty.
	 * 
	 */
	private void setTextFieldEmpty() {
		setTextField("");
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is a help command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayHelpCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		listDisplay.setCurrentPageIndex(HELP_DOC_PAGE_INDEX);
		
		page[HELP_DOC_PAGE_INDEX].getChildren().clear();
		
		setHelpHomePage();
		
		setDisplayTitleText();
		setRestTaskResponse();
		listDisplay.requestFocus();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is search command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displaySearchCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (searchKey != null) {
			setOnePageView(SEARCH_RESULT_PAGE_INDEX, taskList.searchTaskByKeyword(searchKey));
		} else {
			listDisplay.setCurrentPageIndex(SEARCH_RESULT_PAGE_INDEX);
		}
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is show all command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayShowAllCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		listDisplay.setCurrentPageIndex(UNDONE_TASKS_PAGE_INDEX);
		setDisplayTitleText();
		setRestTaskResponse();
		setOnePageView(UNDONE_TASKS_PAGE_INDEX);
	}
	//@author A0119414L
	/**
	 * Display to the view if it is show done command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayShowDoneCommand() throws TaskInvalidDateException, TaskNoSuchTagException {		
		setOnePageView(DONE_TASKS_PAGE_INDEX, taskList.getFinishedTasks());
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is show overdue command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayShowOverdueCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		setOnePageView(OVERDUE_TASKS_PAGE_INDEX, taskList.getOverdueTask());
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is show tag command.
	 * 
	 * @throws TaskNoSuchTagException
	 * @throws TaskInvalidDateException
	 */
	private void displayShowTagCommand() throws TaskNoSuchTagException, TaskInvalidDateException {
		if (showTag != null) {
			if (taskList.isTagContained(showTag)) {
				List<Task> tagTasks = taskList.getTasksWithTag(showTag);
				setOnePageView(TASKS_WITH_TAG_PAGE_INDEX, tagTasks);
			} else {
				List<Task> tagTasks = new ArrayList<Task>();
				setOnePageView(TASKS_WITH_TAG_PAGE_INDEX, tagTasks);
			}
		} else {
			listDisplay.setCurrentPageIndex(TASKS_WITH_TAG_PAGE_INDEX);
		}
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is show period command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayShowPeriodCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		if (showPeriod != null) {
			displayPeriodTasks();
		} else {
			listDisplay.setCurrentPageIndex(PERIOD_TASKS_PAGE_INDEX);
		}
		
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is show today command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
	private void displayShowTodayCommand() throws TaskInvalidDateException, TaskNoSuchTagException {
		List<Task> todayTasks = getTodayTaskList();
		
		setOnePageView(TODAY_TASKS_PAGE_INDEX, todayTasks);
		setDisplayTitleText();
		setRestTaskResponse();
	}
	
	//@author A0119414L
	/**
	 * Display to the view if it is not show, search or help command.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
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
	
	//@author A0119414L
	/**
	 * Check the type of command. 
	 * 
	 * @param command		User input command.
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
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
	
	//@author A0119414L
	/**
	 * Call Logic to check if it is a show date command.
	 * 
	 * @param userCommand		User input command.
	 * @return
	 */
	private boolean isShowDateCommand(String userCommand) {
		return Logic.isShowDateCommand(userCommand);
	}
	
	//@author A0119414L
	/**
	 * Add the command to the history commands after executing it.
	 * 
	 * @param commandToAdd		Command to be added.
	 */
	private void resetHistoryCommands(String commandToAdd) {
		
		historyCommands.add(commandToAdd);
		historyPointer = historyCommands.size();
		
	}
	
	//@author A0119414L
	/**
	 * Find the position of the task with given index in current page.
	 * 
	 * @param specificTaskList		Task list displayed in current page.
	 * @param indexOfTask			Index of the task.
	 * @return						Return the position height of that task.
	 */
	private double findIndexPosition(List<Task> specificTaskList, int indexOfTask) {
		int firstFloatIndex = taskList.indexOfFirstFloatingTask(specificTaskList);
		double position;
		
		if (listDisplay.getCurrentPageIndex()!=SEARCH_RESULT_PAGE_INDEX && listDisplay.getCurrentPageIndex()!=DONE_TASKS_PAGE_INDEX) {
			if (firstFloatIndex == 0) {
				position = 30 + 70*(indexOfTask-1);
				return position;
			} else {
				if (firstFloatIndex == -1) {
					position = 30;
					for (int i=0; i<indexOfTask-1; i++) {
						if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
							position = position + 100;
						} else {
							position = position + 130;
						}
					}
					return position;
				} else {
					if (indexOfTask < firstFloatIndex+1) {
						position = 30;
						for (int i=0; i<indexOfTask-1; i++) {
							if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
								position = position + 100;
							} else {
								position = position + 130;
							}
						}
						return position;
					} else {
						position = 60;
						for (int i=0; i<firstFloatIndex; i++) {
							if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
								position = position + 100;
							} else {
								position = position + 130;
							}
						}
						position = position + 70*(indexOfTask-firstFloatIndex-1);
						return position;
					}
				}
			}
		// Done page and Search page
		} else {
			if (firstFloatIndex == 0) {
				position = 0 + 70*(indexOfTask-1);
				return position;
			} else {
				if (firstFloatIndex == -1) {
					position = 0;
					for (int i=0; i<indexOfTask-1; i++) {
						if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
							position = position + 100;
						} else {
							position = position + 130;
						}
					}
					return position;
				} else {
					if (indexOfTask < firstFloatIndex+1) {
						position = 0;
						for (int i=0; i<indexOfTask-1; i++) {
							if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
								position = position + 100;
							} else {
								position = position + 130;
							}
						}
						return position;
					} else {
						position = 0;
						for (int i=0; i<firstFloatIndex; i++) {
							if (specificTaskList.get(i).getType().equals(Type.DEADLINE)) {
								position = position + 100;
							} else {
								position = position + 130;
							}
						}
						position = position + 70*(indexOfTask-firstFloatIndex-1);
						return position;
					}
				}
			}
		}
	}
	
	//@author A0119414L
	/**
	 * Move the view to display specified position.
	 * 
	 * @param positionMoveTo	Height of position.
	 */
	private void moveToSpecificPosition(double positionMoveTo) {
		int currentPageIndex = listDisplay.getCurrentPageIndex();
		double totalHeight = page[currentPageIndex].getHeight();
		
		scrollPage[currentPageIndex].setVvalue((positionMoveTo)/(totalHeight-338));
	}
	
	//@author A0119414L
	@FXML
	/**
	 * Set the event if the enter key is typed.
	 * 
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
    private void onEnter() throws TaskInvalidDateException, TaskNoSuchTagException {
		command = getUserInput();
		resetHistoryCommands(command);
		
		if (!command.equals("")) {			
			
			if (!isSpecialCommand()) {
				feedback = executeCommand(command);
				taskList = getTaskList();
				taskList.checkOverdue();
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
	
	//@author A0119414L
	@FXML
	/**
	 * Set the event is going to happen when some keys are typed.
	 * 
	 * @param keyEvent		Event happened.
	 * @throws TaskInvalidDateException
	 * @throws TaskNoSuchTagException
	 */
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
	
	//@author A0119414L
	/**
	 * Set comma key to control page up.
	 * 
	 */
	private void setPageUpKey() {
		listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (((event.getCode() == KeyCode.COMMA) && (event.getEventType().equals(KeyEvent.KEY_PRESSED)))) {
					int currentPageIndex = listDisplay.getCurrentPageIndex();
					double currentVvalue = scrollPage[currentPageIndex].getVvalue();
					scrollPage[currentPageIndex].setVvalue(currentVvalue - 0.2);
				}
			}
		});
	}
	
	//@author A0119414L
	/**
	 * Set period key to control page down.
	 * 
	 */
	private void setPageDownKey() {
		listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (((event.getCode() == KeyCode.PERIOD) && (event.getEventType().equals(KeyEvent.KEY_PRESSED)))) {
					int currentPageIndex = listDisplay.getCurrentPageIndex();
					double currentVvalue = scrollPage[currentPageIndex].getVvalue();
					scrollPage[currentPageIndex].setVvalue(currentVvalue + 0.2);
				}
			}
		});
	}
	
	//@author A0119414L
	/**
	 * Set the event when up key is typed.
	 * 
	 */
	private void setUpKey() {
		input.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if ((event.getCode() == KeyCode.UP) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
					if (!historyCommands.isEmpty() && historyPointer > 0) {
						historyPointer = historyPointer - 1;
						input.setText(historyCommands.get(historyPointer));
					}
					input.end();
				}
			}
			
		});
	}
	
	//@author A0119414L
	/**
	 * Set the event when down key is typed.
	 * 
	 */
	private void setDownKey() {
		input.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if ((event.getCode() == KeyCode.DOWN) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
					if (!historyCommands.isEmpty() && historyPointer < historyCommands.size()-1) {
						historyPointer = historyPointer + 1;
						input.setText(historyCommands.get(historyPointer));
					} else if (historyPointer == historyCommands.size()-1) {
						historyPointer = historyPointer + 1;
						input.setText("");
					}
					input.end();
				}
			}
		});
	}

	//@author A0119414L
	/**
	 * Set the event of the left key is typed.
	 * 
	 */
	private void setLeftKey() {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if ((event.getCode() == KeyCode.LEFT) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
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
			});
		}
	
	//@author A0119414L
	/**
	 * Set the event if right key is typed.
	 * 
	 */
	private void setRightKey() {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if ((event.getCode() == KeyCode.RIGHT) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
	//		        	event.consume();
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
			});
		}

	//@author A0119414L
	/**
	 * Set the event when Esc key is typed.
	 * 
	 */
	private void setEscKey() {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if ((event.getCode() == KeyCode.ESCAPE) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
	//		        	event.consume();
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
			});
		}
	
	//@author A0119414L
	/**
	 * Set the event if one of F1 to F8 keys is typed.
	 * 
	 * @param index
	 */
	private void setFKey(final int index) {
			listDisplay.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			     @Override 
			     public void handle(KeyEvent event) {
			        if ((event.getCode() == F_KEYS[index]) && (event.getEventType().equals(KeyEvent.KEY_RELEASED))) {
	//		        	event.consume();
			        	if (listDisplay.getCurrentPageIndex() == HELP_DOC_PAGE_INDEX) {
		    				setHelpPage(index);
		    			}
			        }
			     }
			});
		}

}
