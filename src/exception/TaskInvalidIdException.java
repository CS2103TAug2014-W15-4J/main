package exception;

public class TaskInvalidIdException extends IndexOutOfBoundsException {
    
    public TaskInvalidIdException() {
    }
    public TaskInvalidIdException(String message) {
        super(message);
    }
    
}
