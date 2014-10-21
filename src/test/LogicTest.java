package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import model.FixedTask;
import model.RepeatedTask;
import model.Task;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Logic;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;

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
    final String MESSAGE_INVALID_DONE = "Error: task(s) already marked done.";
    
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

        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);

        // order after adding: deadline, repeating, fixed, floating
        addTasks();

        // testing add for an empty message
        feedback = Logic.readAndExecuteCommands("add");
        assertEquals(feedback, MESSAGE_FAIL);


        // test the adding of a floating task (only description given)
        Task floatingTask = Logic.getTask(3);
        assertEquals(floatingTask.getDescription(), "testing floatingtask");


        // test the adding of a deadline task (description and one date given)
        Task deadlineTask = Logic.getTask(0);
        assertEquals(deadlineTask.getDescription(), "testing");
        assertEquals(deadlineTask.getDeadline(), deadline);


        // test the adding of a fixed task
        FixedTask fixedTask = (FixedTask) Logic.getTask(2);
        assertEquals(fixedTask.getDescription(), "meeting");
        assertEquals(fixedTask.getStartTime(), startTime);
        assertEquals(fixedTask.getDeadline(), endTime);


        // test the adding of a repeated task
        RepeatedTask repeatedTask = (RepeatedTask) Logic.getTask(1);
        assertEquals(repeatedTask.getDescription(), "repeatingtask test");
        assertEquals(repeatedTask.getDeadline(), endTime);
        assertEquals(repeatedTask.getRepeatPeriod(), "every MON");
    }
    
    @Test
    public void testEdit() throws TaskInvalidDateException {

        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);

        // order after adding: deadline, repeating, fixed, floating
        addTasks();


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
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);

        Task floatingTask = Logic.getTask(3);
        assertEquals(floatingTask.getDescription(), "edited description");


        // test the editing of deadline task (only time)
        feedback = Logic.readAndExecuteCommands("edit 1 8 October 2013 3pm");
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);

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
        feedback = Logic.readAndExecuteCommands("edit 2 8 october 2013 3pm");
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);

        RepeatedTask repeatedTask = (RepeatedTask) Logic.getTask(0);
        assertEquals(repeatedTask.getRepeatPeriod(), "every TUE");
        assertEquals(repeatedTask.getDeadline(), deadline);


        // test the editing of fixed task (both description and time)
        feedback = Logic.readAndExecuteCommands("edit 3 lalala 3pm 8 oct 2013 to 13 oct 2014 5pm");
        assertEquals(feedback, MESSAGE_EDIT_SUCCESS);

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


        // testing the invalid marking done of an already done task
        feedback = Logic.readAndExecuteCommands("done 1");
        assertEquals(feedback, MESSAGE_INVALID_DONE);
    }
    
    @Test
    public void testTagging() {
        // clears the task list
        Logic.readAndExecuteCommands(COMMAND_CLEAR);

        // add some tasks for tagging
        addTasks();
        Task task = Logic.getTask(1);


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
        assertEquals(feedback, MESSAGE_REPEAT_TAG_SUCCESS);
    }
    
    /**
     * this method adds one of each type of task into the task list.
     */
    private void addTasks() {
        // add the tasks
        // order after adding: deadline, repeating, fixed, floating
        Logic.readAndExecuteCommands("add testing floatingtask");
        Logic.readAndExecuteCommands("add testing by 13 Oct 2014 4pm");
        Logic.readAndExecuteCommands("add meeting 4pm 13 Oct 2014 to 5pm 13 Oct 2014");
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
