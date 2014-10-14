package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Logic;

public class LogicTest {
    final String MESSAGE_SUCCESS = "Task added successfully.";
    final String MESSAGE_FAIL = "Invalid command. Type 'help' to see the list of available commands.";
    final String MESSAGE_EMPTY = "Your task list is empty.";
    final String COMMAND_SHOW = "show";
    
    String feedback;
    String show;
    
    @BeforeClass
    public static void reset() {
        // set empty task list
        Logic.setEmptyTaskList();
    }
    
    @Test
    public void testAdd() {
        final String MESSAGE_SUCCESS = "Task added successfully.";
        final String MESSAGE_FAIL = "Invalid command. Type 'help' to see the list of available commands.";
        final String MESSAGE_EMPTY = "Your task list is empty.";
        final String COMMAND_SHOW = "show";
        
        // clears the task list
        Logic.readAndExecuteCommands("clear");
        
        // test the adding nothing
        feedback = Logic.readAndExecuteCommands("add");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        System.out.println(show);
        assertEquals(feedback, MESSAGE_FAIL);
        assertEquals(show, MESSAGE_EMPTY);
        
        // test the adding of a floating task
        feedback = Logic.readAndExecuteCommands("add testing floatingtask");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n");
        
        // test the adding of a deadline task
        feedback = Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW); 
        
        assertEquals(feedback, MESSAGE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n");
        
        // test the adding of a fixed task
        feedback = Logic.readAndExecuteCommands("add do this task on 14Oct 2013 4pm to 6pm");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. do this task on 14Oct 2013\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n");
    }
    
    @Test
    public void testEdit() {
        
        // clears the task list
        Logic.readAndExecuteCommands("clear");
        
        // add some tasks for edit
        Logic.readAndExecuteCommands("add testing floatingtask");
        Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        Logic.readAndExecuteCommands("add do this task on 14Oct 2013 4pm to 6pm");
        
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. do this task on 14Oct 2013\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n");
        
        // test the editing of description
        feedback = Logic.readAndExecuteCommands("edit 1 edited description");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, "Task edited successfully.");
        assertEquals(show, "Current tasks:\n1. edited description\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. do this task on 14Oct 2013\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n");
        
        // test the editing of time for deadline task
        feedback = Logic.readAndExecuteCommands("edit 2 8 October 2013 3pm");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, "Task edited successfully.");
        assertEquals(show, "Current tasks:\n1. edited description\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2013-10-08 15:00\nTags: None\nStatus: Ongoing\n\n3. do this task on 14Oct 2013\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n");
        
    }
    
}
