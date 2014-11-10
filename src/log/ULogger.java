package log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//@author A0119446B
/**
 * This class is used to write logging information to the default log file ('uClear.log').
 * This class adapt the singleton pattern.
 * 
 * @author A0119446B
 */
public class ULogger {
	
	// the shared logger singleton
	private static ULogger logger; 
	// the actual logger
	private Logger logWriter;
	// file writer
	private static FileHandler fileHandler;
	
	/**
	 * The private constructor to generate an instance.
	 * 
	 */
	private ULogger() {
		logWriter = Logger.getLogger("uClear");
		// prepare file handler
		try {  
	        // This block configure the logger with handler and formatter  
			fileHandler = new FileHandler("uClear.log");  
	        logWriter.addHandler(fileHandler);
	        logWriter.setUseParentHandlers(false);
	        
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fileHandler.setFormatter(formatter);  
	        
	        // the following statement is used to log a initial message
	        logWriter.info("Logger is ready.");  

	    } catch (SecurityException e) {  
	    	System.out.println("Security failed.");
	    } catch (IOException e) {  
	    	System.out.println("Failed to write log file.");
	    }  
	}
	
	public static final ULogger getLogger() {
		if (logger == null) {
			logger = new ULogger();
		}
		return logger;
	}
	
	/**
	 * This method write a information message into the log file.
	 * 
	 * @param message	The information message
	 */
	public void info(String message) {
		this.logWriter.info(message);
	}
	
	/**
	 * This method write a warning message into the log file.
	 * 
	 * @param message	The warning message
	 */
	public void warning(String message) {
		this.logWriter.warning(message);
	}
	
	/**
	 * This method write a error message into the log file.
	 * 
	 * @param message	The error message
	 */
	public void error(String message) {
		this.logWriter.severe(message);
	}
	
	/**
	 * This method close the file writer. It should be called when the system exits.
	 * 
	 */
	public static void close() {
		logger = null;
		fileHandler.close();
	}
	
}
