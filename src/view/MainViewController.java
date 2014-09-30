package view;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainViewController {
	
	@FXML
	Label date;
	
	@FXML
	VBox toBeCompleted;
	
	@FXML
	VBox toDo;
	
	@FXML
	TextField input;
	
	public void initialize() {
		date.setText(getCurrentDate());
	}
	
	private String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		return dateFormat.format(currentDate);
	}
	
	
}
