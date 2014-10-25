package controller;

import java.util.ArrayList;
import java.util.List;

import model.Task;

public class LastState {
    public enum LastCommand {
        ADD, DELETE, EDIT, DONE, TAG, UNTAG
    }
    LastCommand lastCommand; 
    List<Task> previousTaskStateList;
    Task previousTaskState;
    List<Integer> taskIndices;
    int taskIndex;
    UserInput lastUserInput;
    
    /**
     * to undo for commands: add,
     * @param cmd
     * @param taskEdited
     * @param taskIndexEdited
     */
    public LastState(LastCommand cmd, Task taskEdited, int taskIndexEdited) {
        lastCommand = cmd;
        previousTaskState = taskEdited;
        previousTaskStateList = null;
        taskIndex = taskIndexEdited;
    }
    
    /**
     * to undo for commands: delete,
     * @param cmd
     * @param tasksEdited
     * @param taskIndexEdited
     */
    public LastState(LastCommand cmd, List<Task> tasksEdited, int taskIndexEdited) {
        lastCommand = cmd;
        previousTaskState = null;
        previousTaskStateList = tasksEdited;
        taskIndex = taskIndexEdited;
        
    }

    public LastState(LastCommand cmd, List<Task> tasksEdited, List<Integer> taskIndicesEdited) {
        lastCommand = cmd;
        previousTaskState = null;
        previousTaskStateList = new ArrayList<Task>(tasksEdited);
        taskIndices = taskIndicesEdited;
    }    

    public LastCommand getLastCommand() {
        return lastCommand;
    }
    
    public Task getPreviousTaskState() {
        return previousTaskState;
    }
    
    public int getTaskIndex() {
        return taskIndex;
    }
    
    public List<Task> getPreviousTaskStateList() {
        return previousTaskStateList;
    }
    
    public List<Integer> getTaskIndices() {
        return taskIndices;
    }
    
    
    
    
}
