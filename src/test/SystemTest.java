package test;

import static org.junit.Assert.*;
import controller.Logic;

import org.junit.BeforeClass;
import org.junit.Test;


public class SystemTest {
	
    final String MESSAGE_ADD_SUCCESS = "Task added successfully.";
    final String MESSAGE_EDIT_SUCCESS = "Task edited successfully.";
    final String MESSAGE_DELETE_SUCCESS = "Task(s) deleted successfully.";
    final String MESSAGE_DONE_SUCCESS = "Task(s) marked done successfully.";
    final String MESSAGE_REPEAT_TAG = "Task already contains this tag.";
    final String MESSAGE_TAG_SUCCESS = "Task tagged successfully.";
    final String MESSAGE_UNTAG_SUCCESS = "Task untagged successfully.";
    final String MESSAGE_FAIL = "Invalid command. Type 'help' to see the list of available commands.";
    final String MESSAGE_EMPTY = "Your task list is empty.";
    final String MESSAGE_INVALID_TASKID = "Invalid taskid(s).";
    final String MESSAGE_CLEAR_SUCCESS = "Task list cleared successfully.";
    
    final String COMMAND_CLEAR = "clear";
    final String COMMAND_ADD_TASK_ONE = "add task one";
    final String COMMAND_ADD_TASK_TWO = "add task two";
    final String COMMAND_ADD_EMPTY = "add";
    final String COMMAND_ADD_TASK_THREE = "add task three";
    final String COMMAND_DELETE_INVALID_ID = "delete 4";
    final String COMMAND_DELETE_THREE = "delete 3";
    final String COMMAND_DONE_INVALID_ID = "done 3";
    final String COMMAND_DONE_TWO = "done 2";
    final String COMMAND_TAG_ONE = "tag 1 one";
    final String COMMAND_TAG_REPEATED = "tag 1 one";
    final String COMMAND_UNTAG_ONE = "untag 1 one";
    final String COMMAND_EDIT_ONE_TO_ZERO = "edit 1 task zero";
    
    String feedback;
	
	//@author A0119414L
    /**
     * Set the task list in the Logic to be empty before testing.
     * 
     */
	@BeforeClass
    public static void reset() {
        Logic.setEmptyTaskList();
    }
	
	//@author A0119414L
	/**
	 * Test the whole system with a series of commands.
	 * 
	 */
	@Test
	public void testSystem() {
		// clear
		feedback = Logic.readAndExecuteCommands(COMMAND_CLEAR);
		assertEquals(feedback, MESSAGE_CLEAR_SUCCESS);
		
		// add task one
		feedback = Logic.readAndExecuteCommands(COMMAND_ADD_TASK_ONE); 
		assertEquals(feedback, MESSAGE_ADD_SUCCESS);
		
		// add task two
		feedback = Logic.readAndExecuteCommands(COMMAND_ADD_TASK_TWO);
		assertEquals(feedback, MESSAGE_ADD_SUCCESS);
		
		// add empty
		feedback = Logic.readAndExecuteCommands(COMMAND_ADD_EMPTY);
		assertEquals(feedback, MESSAGE_FAIL);
		
		// add task three
		feedback = Logic.readAndExecuteCommands(COMMAND_ADD_TASK_THREE);
		assertEquals(feedback, MESSAGE_ADD_SUCCESS);
		
		// delete invalid id
		feedback = Logic.readAndExecuteCommands(COMMAND_DELETE_INVALID_ID);
		assertEquals(feedback, MESSAGE_INVALID_TASKID);
		
		// delete three
		feedback = Logic.readAndExecuteCommands(COMMAND_DELETE_THREE);
		assertEquals(feedback, MESSAGE_DELETE_SUCCESS);
		
		// done invalid id
		feedback = Logic.readAndExecuteCommands(COMMAND_DONE_INVALID_ID);
		assertEquals(feedback, MESSAGE_INVALID_TASKID);
		
		// done two
		feedback = Logic.readAndExecuteCommands(COMMAND_DONE_TWO);
		assertEquals(feedback, MESSAGE_DONE_SUCCESS);
		
		// tag task one with one
		feedback = Logic.readAndExecuteCommands(COMMAND_TAG_ONE);
		assertEquals(feedback, MESSAGE_TAG_SUCCESS);
		
		// tag the tag that has already been tagged 
		feedback = Logic.readAndExecuteCommands(COMMAND_TAG_REPEATED);
		assertEquals(feedback, MESSAGE_REPEAT_TAG);
		
		// untag one
		feedback = Logic.readAndExecuteCommands(COMMAND_UNTAG_ONE);
		assertEquals(feedback, MESSAGE_UNTAG_SUCCESS);
		
		// edit one
		feedback = Logic.readAndExecuteCommands(COMMAND_EDIT_ONE_TO_ZERO);
		assertEquals(feedback, MESSAGE_EDIT_SUCCESS);
		
		// clear
		feedback = Logic.readAndExecuteCommands(COMMAND_CLEAR);
		assertEquals(feedback, MESSAGE_CLEAR_SUCCESS);
	}
	
}
