package controller;

import java.util.List;

import model.Task;

//@author A0115384H
/**
 * The class that stores the state of the task list before and after a change.
 * This class is mainly used to support the undo/redo commands.
 */
public class LastState {
    public enum LastCommand {
        ADD, DELETE, EDIT, DONE, TAG, UNTAG, CLEAR
    }
    
    LastCommand lastCommand; 
    List<Task> previousTaskStateList;
    List<Task> currentTaskStateList;
    List<Task> newRepeatTaskList;
    Task previousTaskState;
    Task currentTaskState;
    String tag;
    
    /**
     * This constructor creates a new LastState object, 
     * setting values that support the undo functionality for the 'add' command.
     * 
     * @param cmd           The command type of the last command.
     * @param taskAdded     The task that was last added.
     */
    public LastState(LastCommand cmd, Task taskAdded) {
        lastCommand = cmd;
        previousTaskState = taskAdded;
    }    

    /**
     * This constructor creates a new LastState object, 
     * setting values that support the undo functionality for the 'edit' command.
     * 
     * @param cmd           The command type of the last command.
     * @param taskPrev      The state of the task before editing.
     * @param taskNext      The state of the task after editing.
     */
    public LastState(LastCommand cmd, Task taskPrev, Task taskNext) {
        lastCommand = cmd;
        previousTaskState = taskPrev;
        currentTaskState = taskNext;
    }
    
    /**
     * This constructor creates a new LastState object, 
     * setting values that support the undo functionality for the 'delete' command.
     * 
     * @param cmd           The command type of the last command.
     * @param taskListPrev  The state of the list of tasks before the deletion.
     */
    public LastState(LastCommand cmd, List<Task> taskListPrev) {
        lastCommand = cmd;
        previousTaskStateList = taskListPrev;
    }
    
    /**
     * This constructor creates a new LastState object, 
     * setting values that support the undo functionality for the '(un)tag' command.
     * 
     * @param cmd          The command type of the last command.
     * @param taskPrev     The state of the task before the (un)tagging.
     * @param taskCurrent  The state of the task after the (un)tagging.
     * @param givenTag     The tag that was (un)tagged.
     */
    public LastState(LastCommand cmd, Task taskPrev,
                     Task taskCurrent, String givenTag) {
        lastCommand = cmd;
        previousTaskState = taskPrev;
        currentTaskState = taskCurrent;
        tag = givenTag;
    }
    
    /**
     * This constructor creates a new LastState object, 
     * setting values that support the undo functionality for the 'done' command.
     * 
     * @param cmd               The command type of the last command.
     * @param taskListPrev      The state of the list of tasks before the marking done.
     * @param taskListCurrent   The state of the list of tasks after the marking done.
     * @param repeatTaskList    The tasks marked done that were RepeatTasks.
     */
    public LastState(LastCommand cmd, List<Task> taskListPrev,
                     List<Task> taskListCurrent, List<Task> repeatTaskList) {
        lastCommand = cmd;
        previousTaskStateList = taskListPrev;
        currentTaskStateList = taskListCurrent;
        newRepeatTaskList = repeatTaskList;
    }

    /**
     * This method returns the command of the LastState object.
     * 
     * @return the LastCommand enum type representing the various commands.
     */
    public LastCommand getLastCommand() {
        return lastCommand;
    }
    
    /**
     * This method returns the last state of the task, prior to the change.
     * 
     * @return the previous state of the task.
     */
 
    public Task getPreviousTaskState() {
        return previousTaskState;
    }
    
    /**
     * This method returns the newer state of the task, prior to the change.
     * 
     * @return the current state of the task.
     */
    public Task getCurrentTaskState() {
        return currentTaskState;
    }

    /** 
     * This method returns the previous state of the list of tasks, prior to the change.
     * 
     * @return the previous state of the list of tasks.
     */
    public List<Task> getPreviousTaskStateList() {
        return previousTaskStateList;
    }

    /** 
     * This method returns the newer state of the list of tasks, after the change.
     * 
     * @return the newer state of the list of tasks.
     */
    public List<Task> getCurrentTaskStateList() {
        return currentTaskStateList;
    }

    /** 
     * This method returns the list of repeated tasks, when marking tasks done.
     * 
     * @return the list of repeated tasks, when marking tasks done.
     */
    public List<Task> getRepeatTaskList() {
        return newRepeatTaskList;
    }
    
    /**
     * This method returns the tag that was (un)tagged, for the tag/untag functionality.
     * 
     * @return the tag that was (un)tagged.
     */
    public String getTag() {
        return tag;
    }
}
