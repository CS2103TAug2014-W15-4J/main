package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import model.DeadlineTask;
import model.FixedTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.Task;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Logic;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;

//@author A0115384H
/**
 * This class is for testing the functionalities of the Logic class.
 */
public class LogicTest {
    final String MESSAGE_ADD_SUCCESS = "Task added successfully.";
    final String MESSAGE_EDIT_DESCRIPTION = "Task \"%s\" is renamed as \"%s\"";
    final String MESSAGE_EDIT_DEADLINE = "The deadline is changed to \"%s\"";
    final String MESSAGE_EDIT_DESCRIPTION_DEADLINE = "Task is renamed as \"%s\". New deadline: \"%s\"";
    final String MESSAGE_DELETE_SUCCESS = "Task(s) deleted successfully.";
    final String MESSAGE_DONE_SUCCESS = "Task(s) marked done successfully.";
    final String MESSAGE_INVALID_REPEAT_TAG = "Task already contains this tag.";
    final String MESSAGE_TAG_SUCCESS = "Task tagged successfully.";    
    final String MESSAGE_UNTAG_SUCCESS = "Task untagged successfully.";
    final String MESSAGE_UNDO_SUCCESS = "undo successful.";
    final String MESSAGE_REDO_SUCCESS = "redo successful.";
    final String MESSAGE_FAIL = "Invalid command. Type 'help' to see the list of available commands.";
    final String MESSAGE_EMPTY = "Your task list is empty.";
    final String MESSAGE_INVALID_TASKID = "Invalid taskid(s).";
    final String MESSAGE_INVALID_DONE = "Error: task(s) already marked done.";
    final String MESSAGE_INVALID_UNDO = "No previous operation to undo.";
    final String MESSAGE_INVALID_REDO = "No next operation to redo.";
    final String MESSAGE_INVALID_UNTAG = "No such tag to remove.";
    
    final String COMMAND_SHOW = "show";
    final String COMMAND_CLEAR = "clear";

    String feedback;
    String show;
    Calendar cal = Calendar.getInstance();
    Date startTime;
    Date endTime;
    Date deadline;
    
    @BeforeClass
    public static void reset() {
        // initialise an empty task list
        Logic.setEmptyTaskList();
    }
    
    @Test
    public void testAdd() throws TaskInvalidDateException {

        // clears the task list and undo/redo stacks
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        Logic.emptyUndoRedoStack();

        // order after adding: deadline, repeating, fixed, floating
        addTasks();

        // testing add for an empty message
        feedback = Logic.readAndExecuteCommands("add");
        assertEquals(feedback, MESSAGE_FAIL);

        for (int i = 0; i < 4; i++) {
            Task task = Logic.getTask(i);
            
            if (task instanceof FloatingTask) {
                // check the adding of a floating task (only description given) is successful
                assertEquals(task.getDescription(), "testing floatingtask");
                
            } else if (task instanceof DeadlineTask) {
                // check the adding of a deadline task (description and one date given) is successful
                assertEquals(task.getDescription(), "testing");
                assertEquals(task.getDeadline(), deadline);
                
            } else if (task instanceof FixedTask) {
                // check the adding of a fixed task (description and two dates given) is successful
                assertEquals(((FixedTask) task).getDescription(), "meeting");
                assertEquals(((FixedTask) task).getStartTime(), startTime);
                assertEquals(((FixedTask) task).getDeadline(), endTime);
                
            } else if (task instanceof RepeatedTask) {
                // check the adding of a repeated task (description, date and repeat period given) is successful
                assertEquals(((RepeatedTask) task).getDescription(), "repeatingtask test");
                assertEquals(((RepeatedTask) task).getDeadline(), endTime);
                assertEquals(((RepeatedTask) task).getRepeatPeriod(), "every MONDAY");
                
            } else {
                assert false;
            }
        }
    }
    
    @Test
    public void testEdit() throws TaskInvalidDateException {

        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // order after adding: deadline, repeating, fixed, floating
        addTasks();
        
        // empty undo/redo stacks
        Logic.emptyUndoRedoStack();

        // trying to edit an invalid task id on boundaries <= 0, >=5
        try {
            Logic.readAndExecuteCommands("edit 0 lalala");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }

        try {
            Logic.readAndExecuteCommands("edit 5 lalala");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }


        // test the editing of floating task (only description)
        feedback = Logic.readAndExecuteCommands("edit 4 edited description");
        assertEquals(feedback, String.format(MESSAGE_EDIT_DESCRIPTION, "testing floatingtask", "edited description"));
        
        Task floatingTask = Logic.getTask(3);
        assertEquals(floatingTask.getDescription(), "edited description");


        // test the editing of deadline task (only time)
        feedback = Logic.readAndExecuteCommands("edit 1 by 8 October 2013 3pm");
        assertEquals(feedback, String.format(MESSAGE_EDIT_DEADLINE, "Tue, Oct 8 15:00"));

        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 9);
        cal.set(Calendar.DAY_OF_MONTH, 8);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        deadline = cal.getTime();

        Task deadlineTask = Logic.getTask(0);
        assertEquals(deadlineTask.getDescription(), "testing");
        assertEquals(deadlineTask.getDeadline(), deadline);


        // test the editing of time for repeat tasks
        // after the edit, the order is changed: repeated, deadline, fixed, floated
        feedback = Logic.readAndExecuteCommands("edit 2 by 8 october 2013 3pm");
        assertEquals(feedback, String.format(MESSAGE_EDIT_DEADLINE, "Tue, Oct 8 15:00"));

        RepeatedTask repeatedTask = (RepeatedTask) Logic.getTask(0);
        assertEquals(repeatedTask.getRepeatPeriod(), "every TUESDAY");
        assertEquals(repeatedTask.getDeadline(), deadline);


        // test the editing of fixed task (both description and time)
        feedback = Logic.readAndExecuteCommands("edit 3 lalala from 3pm 8 oct 2013 to 13 oct 2014 5pm");
        assertEquals(feedback, String.format(MESSAGE_EDIT_DESCRIPTION_DEADLINE, "lalala", "Mon, Oct 13 17:00"));

        FixedTask fixedTask = (FixedTask) Logic.getTask(2); 

        assertEquals(fixedTask.getDescription(), "lalala");
        assertEquals(fixedTask.getDeadline(), endTime);
        assertEquals(fixedTask.getStartTime(), deadline);
    }
    
    @Test
    public void testDelete() {

        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);

        // order after adding: deadline, repeating, fixed, floating
        addTasks();
        
        // empty undo/redo stacks
        Logic.emptyUndoRedoStack();

        // trying to edit an invalid task id on boundaries <= 0, >=5
        try {
            Logic.readAndExecuteCommands("delete 0");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }

        try {
            Logic.readAndExecuteCommands("delete 5");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }


        // testing of delete for one task
        Task task = Logic.getTask(1);
        feedback = Logic.readAndExecuteCommands("delete 2");
        assertEquals(feedback, MESSAGE_DELETE_SUCCESS);
        assertNotEquals(task, Logic.getTask(1));


        // testing of delete for multiple tasks
        Task task1 = Logic.getTask(0);
        Task task2 = Logic.getTask(1);
        feedback = Logic.readAndExecuteCommands("delete 1 2");
        assertEquals(feedback, MESSAGE_DELETE_SUCCESS);
        assertNotEquals(task1, Logic.getTask(0));
        assertNotEquals(task2, Logic.getTask(0));

    }
    
    @Test
    public void testMarkDone() {
        
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);
        
        // add some tasks for marking done
        addTasks();
        
        // empty undo/redo stacks
        Logic.emptyUndoRedoStack();

        // trying to mark done an invalid task id on boundaries <= 0, >=5
        try {
            Logic.readAndExecuteCommands("done 0");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }

        try {
            Logic.readAndExecuteCommands("done 5");
            assert false;
        } catch (TaskInvalidIdException e) {
            assert true;
        }


        // ensure the tasks are non-marked done
        Task task1 = Logic.getTask(0);
        Task task2 = Logic.getTask(1);
        Task task3 = Logic.getTask(2);
        Task task4 = Logic.getTask(3);

        assert !task1.getIsDone();
        assert !task2.getIsDone();
        assert !task3.getIsDone();
        assert !task4.getIsDone();


        // testing of marking done for one task
        feedback = Logic.readAndExecuteCommands("done 2");
        assertEquals(feedback, MESSAGE_DONE_SUCCESS);
        assert task1.getIsDone();


        // testing of marking done for multiple tasks
        feedback = Logic.readAndExecuteCommands("done 1 3");
        assertEquals(feedback, MESSAGE_DONE_SUCCESS);
        assert task1.getIsDone();
        assert task3.getIsDone();
    }
    
    @Test
    public void testTagging() {
        
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);

        // add some tasks for tagging
        addTasks();
        Task task = Logic.getTask(1);

        // empty undo/redo stacks
        Logic.emptyUndoRedoStack();

        // testing to add an empty tag
        feedback = Logic.readAndExecuteCommands("tag 2");
        assertEquals(feedback, MESSAGE_FAIL);


        // testing to add a tag
        feedback = Logic.readAndExecuteCommands("tag 2 thiss");
        assertEquals(feedback, MESSAGE_TAG_SUCCESS);
        assert task.getTags().contains("thiss".toLowerCase());


        // testing to add another tag
        feedback = Logic.readAndExecuteCommands("tag 2 third eye");
        assertEquals(feedback, MESSAGE_TAG_SUCCESS);
        assert task.getTags().contains("third eye".toLowerCase());


        // testing to add an invalid repeated tag (shows non-case sensitive as well)
        feedback = Logic.readAndExecuteCommands("tag 2 THIRD EYE");
        assertEquals(feedback, MESSAGE_INVALID_REPEAT_TAG);
        
        // testing to remove a non-existent tag
        feedback = Logic.readAndExecuteCommands("untag 2 lalala");
        assertEquals(feedback, MESSAGE_INVALID_UNTAG);
        
        // testing to remove a valid tag
        feedback = Logic.readAndExecuteCommands("untag 2 THIRD EYE");
        assertEquals(feedback, MESSAGE_UNTAG_SUCCESS);
        assertFalse(task.getTags().contains("third eye"));
    }
    
    @Test
    public void testUndoRedo() throws TaskInvalidDateException {
   
        // resets the task list and the undo/redo stacks
        Logic.emptyUndoRedoStack();
        Logic.setEmptyTaskList();
       
        
        // testing the invalid undo/redo of a new tasklist
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);
        

        // testing the undo/redo functionalities of add
        testAdd();
        feedback = Logic.readAndExecuteCommands("undo"); // undo add: repeated task
        feedback = Logic.readAndExecuteCommands("undo"); // undo add: fixed task
        feedback = Logic.readAndExecuteCommands("undo"); // undo add: deadline task
        feedback = Logic.readAndExecuteCommands("undo"); // undo add: floating task
        assertEquals(feedback, MESSAGE_UNDO_SUCCESS);
       
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_REDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);

        
        // testing the undo/redo functionalities of edit
        testEdit();
        feedback = Logic.readAndExecuteCommands("undo"); // undo edit: fixed task
        feedback = Logic.readAndExecuteCommands("undo"); // undo edit: repeat task
        feedback = Logic.readAndExecuteCommands("undo"); // undo edit: deadline task
        feedback = Logic.readAndExecuteCommands("undo"); // undo edit: floating task
        assertEquals(feedback, MESSAGE_UNDO_SUCCESS);
       
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_REDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);
        
        
        // testing the undo/redo functionalities of delete
        testDelete();
        feedback = Logic.readAndExecuteCommands("undo"); // undo delete: multiple tasks
        feedback = Logic.readAndExecuteCommands("undo"); // undo delete: one task
        assertEquals(feedback, MESSAGE_UNDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_REDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);

        
        // testing the undo/redo functionalities of mark done
        testMarkDone();
        feedback = Logic.readAndExecuteCommands("undo"); // undo mark done: multiple tasks
        feedback = Logic.readAndExecuteCommands("undo"); // undo mark done: one task
        assertEquals(feedback, MESSAGE_UNDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_REDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);
        
        
        // testing the undo/redo functionalities of tagging
        testTagging();
        feedback = Logic.readAndExecuteCommands("undo"); // undo untagging from task
        feedback = Logic.readAndExecuteCommands("undo"); // undo a second tag on a task
        feedback = Logic.readAndExecuteCommands("undo"); // undo a first tag on a task 
        assertEquals(feedback, MESSAGE_UNDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("undo");
        assertEquals(feedback, MESSAGE_INVALID_UNDO);
        
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_REDO_SUCCESS);
        
        feedback = Logic.readAndExecuteCommands("redo");
        assertEquals(feedback, MESSAGE_INVALID_REDO);

    }
    
    /**
     * this method adds one of each type of task into the task list.
     */
    private void addTasks() {
        // add the tasks
        // order after adding: deadline, repeating, fixed, floating
        Logic.readAndExecuteCommands("add testing floatingtask");
        Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        Logic.readAndExecuteCommands("add meeting from 4pm 13 Oct 2014 to 5pm 13 Oct 2014");
        Logic.readAndExecuteCommands("add repeatingtask test every 5pm oct 13 weekly");

        // set the date values.
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, 9);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        deadline = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 17);
        
        startTime = deadline;
        endTime = cal.getTime(); 
    }
}
