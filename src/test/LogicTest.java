package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Logic;

public class LogicTest {
    final String MESSAGE_ADD_SUCCESS = "Task added successfully.";
    final String MESSAGE_EDIT_SUCCESS = "Task edited successfully.";
    final String MESSAGE_DELETE_SUCCESS = "Task(s) deleted successfully.";
    final String MESSAGE_DONE_SUCCESS = "Task(s) marked done successfully.";
    final String MESSAGE_REPEAT_TAG_SUCCESS = "Task already contains this tag.";
    final String MESSAGE_TAG_SUCCESS = "Task tagged successfully.";
    final String MESSAGE_UNTAG_SUCCESS = "Task untagged successfully.";
    final String MESSAGE_FAIL = "Invalid command. Type 'help' to see the list of available commands.";
    final String MESSAGE_EMPTY = "Your task list is empty.";
    final String MESSAGE_INVALID_TASKID = "Invalid taskid(s).";
    final String COMMAND_SHOW = "show";
    final String COMMAND_CLEAR = "clear";

    String feedback;
    String show;
    
    @BeforeClass
    public static void reset() {
        // set empty task list
        Logic.setEmptyTaskList();
    }
    
    @Test
    public void testAdd() {

        
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // test the adding nothing
        feedback = Logic.readAndExecuteCommands("add");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);

        assertEquals(feedback, MESSAGE_FAIL);
        assertEquals(show, MESSAGE_EMPTY);
        
        // test the adding of a floating task
        feedback = Logic.readAndExecuteCommands("add testing floatingtask");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_ADD_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing");
        
        // test the adding of a deadline task
        feedback = Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW); 
        
        assertEquals(feedback, MESSAGE_ADD_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing");
        
        // test the adding of a fixed task
        feedback = Logic.readAndExecuteCommands("add meeting 4pm 14 Oct 2014 to 6pm 14 Oct 2014");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_ADD_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing");
    }
    
    @Test
    public void testEdit() {
        
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // add some tasks for edit
        Logic.readAndExecuteCommands("add testing floatingtask");
        Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        Logic.readAndExecuteCommands("add meeting 4pm 14 Oct 2014 to 6pm 14 Oct 2014");
        
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing");
        
        // test the editing of description
        feedback = Logic.readAndExecuteCommands("edit 1 edited description");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);
        assertEquals(show, "Current tasks:\n1. edited description\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing");
        
        // test the editing of time for deadline task
        feedback = Logic.readAndExecuteCommands("edit 2 8 October 2013 3pm");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);
        assertEquals(show, "Current tasks:\n1. edited description\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2013-10-08 15:00\nTags: None\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing");
        
    }
    
    @Test
    public void testDelete() {
        
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // add some tasks for deleting
        addTasks();
        
        // testing of delete for invalid task
        feedback = Logic.readAndExecuteCommands("delete -2");
        assertEquals(feedback, MESSAGE_FAIL);

        feedback = Logic.readAndExecuteCommands("delete 5");
        assertEquals(feedback, MESSAGE_INVALID_TASKID);
        
        // testing of delete for one task
        feedback = Logic.readAndExecuteCommands("delete 2");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        
        assertEquals(feedback, MESSAGE_DELETE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n\n3. lalalalala\nTags: None\nStatus: Ongoing");
        
        // testing of delete for multiple tasks
        feedback = Logic.readAndExecuteCommands("delete 1 2");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(feedback, MESSAGE_DELETE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. lalalalala\nTags: None\nStatus: Ongoing");

    }
    
    @Test
    public void testMarkDone() {
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // add some tasks for marking done
        addTasks();
        
        // testing of marking done for invalid tasks
        feedback = Logic.readAndExecuteCommands("done -2");
        assertEquals(feedback, MESSAGE_FAIL);

        feedback = Logic.readAndExecuteCommands("done 5");
        assertEquals(feedback, MESSAGE_INVALID_TASKID);

        // testing of marking done for one task
        feedback = Logic.readAndExecuteCommands("done 2");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(feedback, MESSAGE_DONE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Done\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n\n4. lalalalala\nTags: None\nStatus: Ongoing");

        // testing of marking done for multiple tasks
        feedback = Logic.readAndExecuteCommands("done 1 3");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(feedback, MESSAGE_DONE_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Done\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Done\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Done\n\n4. lalalalala\nTags: None\nStatus: Ongoing");
        
    }
    
    @Test
    public void testTagging() {
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // add some tasks for marking done
        addTasks();
        
        // testing to add a tag
        feedback = Logic.readAndExecuteCommands("tag 2 thiss");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(feedback, MESSAGE_TAG_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: thiss\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n\n4. lalalalala\nTags: None\nStatus: Ongoing");
        
        // testing to add another tag
        feedback = Logic.readAndExecuteCommands("tag 2 third");
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(feedback, MESSAGE_TAG_SUCCESS);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: thiss, third\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n\n4. lalalalala\nTags: None\nStatus: Ongoing");
        
        // testing to add a repeated tag
        feedback = Logic.readAndExecuteCommands("tag 2 third");
        assertEquals(feedback, MESSAGE_REPEAT_TAG_SUCCESS);

    }
    
    private void addTasks() {
        Logic.readAndExecuteCommands("add testing floatingtask");
        Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        Logic.readAndExecuteCommands("add meeting 4pm 14 Oct 2014 to 6pm 14 Oct 2014");
        Logic.readAndExecuteCommands("add lalalalala");
        
        show = Logic.readAndExecuteCommands(COMMAND_SHOW);
        assertEquals(show, "Current tasks:\n1. testing floatingtask\nTags: None\nStatus: Ongoing\n\n2. testing\nDue: 2014-10-13 16:00\nTags: None\nStatus: Ongoing\n\n3. meeting\nStart: 2014-10-14 16:00\nDue: 2014-10-14 18:00\nTags: None\nStatus: Ongoing\n\n4. lalalalala\nTags: None\nStatus: Ongoing");

    }
}
