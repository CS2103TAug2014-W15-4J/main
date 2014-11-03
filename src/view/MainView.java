package view;
	
import java.io.IOException;
import java.util.Set;

import exception.TaskInvalidDateException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.text.*;


public class MainView extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException, TaskInvalidDateException {
		MainViewController mvc = new MainViewController();
		
		Scene scene = new Scene(mvc, 900, 620);
		Font.loadFont(MainView.class.getResource("Roboto-Regular.ttf").toExternalForm(), 10);
		scene.getStylesheets().add(getClass().getResource("MainView.css").toExternalForm());
		primaryStage.setTitle("uClear");
		primaryStage.getIcons().add(new Image("/view/uClear-icon.png"));
		
		if (getOS().equals("Mac OS X")) {
			primaryStage.setMaxHeight(642);
			primaryStage.setMaxWidth(900);
		} else {
			primaryStage.setMaxHeight(658);
			primaryStage.setMaxWidth(916);
		}
		
		primaryStage.setMinHeight(primaryStage.getMaxHeight());
		primaryStage.setMinWidth(primaryStage.getMaxWidth());
		primaryStage.centerOnScreen();
		primaryStage.setScene(scene);
		primaryStage.show();
		
		setScrollBar(mvc);
	}
	
	private String getOS() {
		return System.getProperty("os.name");
	}
	
	private ScrollPane[] getScrollPages(MainViewController mvc) {
		return mvc.getScrollPages();
	}
	
	private void setScrollBar(MainViewController mvc) {
		ScrollPane[] scrollPage = getScrollPages(mvc);
		ScrollBar[] scrollBar = new ScrollBar[scrollPage.length];
		
		for (int i=0; i<scrollPage.length; i++) {
			Set<Node> scrollBars = scrollPage[i].lookupAll(".scroll-bar");
	        for (final Node sBar : scrollBars) {
	            if (sBar instanceof ScrollBar) {
	                scrollBar[i] = (ScrollBar) sBar;
	            }
	        }
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

