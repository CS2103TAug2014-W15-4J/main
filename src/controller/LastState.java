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
    
    /**
     * to undo for command: add
     * 
     * @param cmd
     * @param tasksEdited
     * @param tag
     */
    public LastState(LastCommand cmd, Task taskEdited) {
        lastCommand = cmd;
        previousTaskState = taskEdited;
    }    

    /**
     * to undo for command: edit
     * 
     * @param cmd
     * @param taskPrev
     * @param taskNext
     */
    public LastState(LastCommand cmd, Task taskPrev, Task taskNext) {
        lastCommand = cmd;
        previousTaskState = taskPrev;
        currentTaskState = taskNext;
    }
    
    /**
     * to undo for command: delete
     * 
     * @param cmd
     * @param taskListPrev
     */
    public LastState(LastCommand cmd, List<Task> taskListPrev) {
        lastCommand = cmd;
        previousTaskStateList = taskListPrev;
    }
    
    /**
     * to undo for command: tag, untag
     * 
     * @param cmd
     * @param taskPrevState
     * @param taskCurrentState
     * @param givenTag
     */
    public LastState(LastCommand cmd, Task taskPrevState,
                     Task taskCurrentState, String givenTag) {
        lastCommand = cmd;
        previousTaskState = taskPrevState;
        currentTaskState = taskCurrentState;
        tag = givenTag;
    }
    
    /**
     * to undo for command: done
     * 
     * @param cmd
     * @param taskListPrevState
     * @param taskListCurrentState
     * @param repeatTaskList
     */
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
}
