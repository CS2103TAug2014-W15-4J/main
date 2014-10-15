package exception;

public class TaskTagException extends Exception {
    
    // exception if trying to do Task.removeTag(tag) on task without tag.
    public TaskTagException(String message) {
        // TODO Auto-generated constructor stub
        super(message);
    }
    
    public TaskTagException() {
     // TODO Auto-generated constructor stub
        super();
    }
    

    
}
