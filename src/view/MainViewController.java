package view;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
	
    @FXML 
    private TextField textField;
    
    @FXML
    private Label date;

    public MainViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
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

//    @FXML
//    protected void doSomething() {
//        System.out.println("The button was clicked!");
//    }
    
//    private String getCurrentDate() {
//    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    	Date currentDate = new Date();
//    	return dateFormat.format(currentDate);
//    }
    
    
    
}


