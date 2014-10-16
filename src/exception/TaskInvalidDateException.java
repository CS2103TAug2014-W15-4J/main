package exception;

public class TaskInvalidDateException extends Exception {

    public TaskInvalidDateException(String message) {
        super(message);
    }
    
    public TaskInvalidDateException() {
        super();
    }
    
}
