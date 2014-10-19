package view;

import controller.Logic;
import model.TaskList;
import model.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import javafx.scene.effect.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainViewController extends GridPane{
	
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
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd  HH : mm : ss");
	final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			final Calendar cal = Calendar.getInstance();
			date.setText(dateFormat.format(cal.getTime()));
		}
	}));

	public MainViewController() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        fxmlLoader.load();
        
        initialize();
	}
	
	private void initialize() {
		setPageCount(5);
		setPages();
		setFont();
        setDate();
        initMainDisplay();
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

	private void initMainDisplay() {
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
	
	private void displayExistingTasks() {
        Logic.initialize();
		taskList = Logic.getTaskList();
		
		for (int i=0; i<taskList.count(); i++) {
			GridPane taskLayout = new GridPane();
			taskLayout.setPrefSize(383, 100);
			
			
		}
	}

	private void changePage(int pageNum) {
		listDisplay.setCurrentPageIndex(pageNum % pageCount);
	}
	
	private void closePage() {
		int currentPageNum = listDisplay.getCurrentPageIndex();
		
		page[currentPageNum] = new VBox();
		
		changePage(currentPageNum+1);
	}
	
	private void setMainDisplay() {
//		int currentPageNum = listDisplay.getCurrentPageIndex();
		
		
	}
	
	private String getUserInput() {
		return input.getText();
	}
	
	private String executeCommand(String command) {
		return Logic.readAndExecuteCommands(command);
	}
	
	@FXML
    private void onEnter() {
		command = getUserInput();
		
		if (command.trim().toLowerCase().equals("close")) {
			closePage();
		}
		
		if (command.trim().toLowerCase().equals("exit")) {
			Logic.saveTaskList();
			Platform.exit();
		}
		
		feedback = executeCommand(command);
		setMainDisplay();
    	input.setText("");
    }
	
}


