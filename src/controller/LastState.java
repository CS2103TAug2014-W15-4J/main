package controller;

import java.util.List;

import model.Task;

public class LastState {
    public enum LastCommand {
        ADD, DELETE, EDIT, DONE, TAG, UNTAG
    }
    LastCommand lastCommand; 
    List<Task> previousTaskStateList;
    List<Task> currentTaskStateList;
    List<Task> newRepeatTaskList;
    Task previousTaskState;
    Task currentTaskState;
    String tag;
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
     * to undo for commands: delete, mark done
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
    
    /**
     * to undo for commands: tag, untag
     * @param cmd
     * @param tasksEdited
     * @param tag
     */
    public LastState(LastCommand cmd, Task taskEdited) {
        lastCommand = cmd;
        previousTaskState = taskEdited;
        previousTaskStateList = null;
    }    


    public LastState(LastCommand cmd, Task taskPrev, Task taskNext) {
        lastCommand = cmd;
        previousTaskState = taskPrev;
        currentTaskState = taskNext;
        previousTaskStateList = null;
        
    }

    public LastState(LastCommand cmd, List<Task> taskListPrevState,
                     List<Task> taskListCurrentState) {
        lastCommand = cmd;
        previousTaskStateList = taskListPrevState;
        currentTaskStateList = taskListCurrentState;
    }
    
    public LastState(LastCommand cmd, Task taskPrevState,
                     Task taskCurrentState, String givenTag) {
        lastCommand = cmd;
        previousTaskState = taskPrevState;
        currentTaskState = taskCurrentState;
        tag = givenTag;
    }
    
    public LastState(LastCommand cmd, List<Task> taskListPrevState,
                     List<Task> taskListCurrentState, List<Task> repeatTaskList) {
        lastCommand = cmd;
        previousTaskStateList = taskListPrevState;
        currentTaskStateList = taskListCurrentState;
        newRepeatTaskList = repeatTaskList;
    }

    public LastCommand getLastCommand() {
        return lastCommand;
    }
    
    public Task getPreviousTaskState() {
        return previousTaskState;
    }
    
    public Task getCurrentTaskState() {
        return currentTaskState;
    }
    
    public int getTaskIndex() {
        return taskIndex;
    }
    
    public List<Task> getPreviousTaskStateList() {
        return previousTaskStateList;
    }
    
    public List<Task> getCurrentTaskStateList() {
        return currentTaskStateList;
    }
    
    public List<Task> getRepeatTaskList() {
        return newRepeatTaskList;
    }
    
    public String getTag() {
        return tag;
    }
    
    public List<Integer> getTaskIndices() {
        return taskIndices;
    }
    
    
    
}
