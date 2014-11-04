package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.FixedTask;
import model.Task;
import model.TaskList;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.UserInput;
import exception.RedoException;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;
import exception.UndoException;

public class TaskListTest {

	static Calendar cal = Calendar.getInstance();
	static Date today;
	static Date tomorrow;
	static Date yesterday;

	@Test
	public void testEmptyList() {
		TaskList tasks = new TaskList();
		assertEquals(0, tasks.count());
	}

	@Test
	public void testIsValidIndex() {
		TaskList tasks = new TaskList();
		assertTrue(tasks.isInvalidIndex(0));
		assertTrue(tasks.isInvalidIndex(-1));
		for (int i = 0; i < 50; i++) {
			tasks.addToList("No." + i);
		}

		/* This is a boundary case for the ‘below valid index’ partition */
		assertTrue(tasks.isInvalidIndex(0));
		/* This is a boundary case for the ‘valid’ partition */
		assertFalse(tasks.isInvalidIndex(27));
		/* This is a boundary case for the ‘valid’ partition */
		assertFalse(tasks.isInvalidIndex(50));
		/* This is a boundary case for the ‘above valid index’ partition */
		assertTrue(tasks.isInvalidIndex(51));
	}
	
	@Test
	public void testIndexOfFirstFloatingTask() {
		TaskList tasks = new TaskList();
		
		// test for unfound case
		for (int i = 0; i < 50; i++) {
			tasks.addToList("Non-floating task", new Date(i));
		}
		
		List<Task> listOfTask = tasks.prepareDisplayList(false);
		assertEquals(50, listOfTask.size());
		
		assertEquals(-1, tasks.indexOfFirstFloatingTask(listOfTask));
		
		// test for found case
		tasks = new TaskList();
		for (int i = 0; i < 50; i++) {
			tasks.addToList("Non-floating task", new Date(i));
		}
		
		for (int i = 0; i < 10; i++) {
			tasks.addToList("Floating task");
		}
		
		listOfTask = tasks.prepareDisplayList(false);
		assertEquals(60, listOfTask.size());
		
		assertEquals(50, tasks.indexOfFirstFloatingTask(listOfTask));
	}

	@Test
	public void testAddingTask() {
		TaskList tasks = new TaskList();
		// generates 50 dummy tasks and adds them
		for (int i = 0; i < 50; i++) {
			tasks.addToList("No." + i);
		}
		assertEquals(50, tasks.count());

		// test the adding of different types of tasks
		tasks = addTasks();
		assertEquals(4, tasks.count());
	}

	@Test
	public void testDeletingTask() {
		TaskList tasks = new TaskList();
		// generate 50 dummy task and add them
		for (int i = 0; i < 50; i++) {
			tasks.addToList("No." + i);
		}
		assertEquals(50, tasks.count());

		// delete single task
		List<Integer> toDelete = new ArrayList<Integer>();
		toDelete.add(1);
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(49, tasks.count());

		// delete multiple task
		toDelete = new ArrayList<Integer>();
		for (int i = 1; i <= 25; i++) {
			toDelete.add(i);
		}
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(24, tasks.count());

		/* This is a boundary case for the ‘valid’ partition */
		toDelete = new ArrayList<Integer>();
		toDelete.add(24);
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(23, tasks.count());

		/* This is a boundary case for the ‘above valid’ partition */
		/* IndexOutOfBoundsException is expected */
		toDelete = new ArrayList<Integer>();
		toDelete.add(24);
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			assertTrue(e instanceof IndexOutOfBoundsException);
		}

		/* This is a boundary case for the ‘below valid’ partition */
		/* IndexOutOfBoundsException is expected */
		toDelete = new ArrayList<Integer>();
		toDelete.add(-1);
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			assertTrue(e instanceof IndexOutOfBoundsException);
		}

		// clear tasks
		tasks.clearList();
		assertEquals(0, tasks.count());

		// test the deleting of different types of tasks
		tasks = addTasks();
		toDelete.clear();
		toDelete.add(1);
		for (int i = 3; i >= 0; i--) {
			tasks.deleteFromList(toDelete);
			assertEquals(tasks.count(), i);
		}
	}

	@Test
	public void testEditingTask() throws TaskInvalidDateException {
		TaskList tasks = new TaskList();
		/* This is a boundary case for the ‘invalid editing’ partition */
		try {
			tasks.editTaskDescriptionOnly(0, "UPPERCASE");
		} catch (TaskInvalidIdException e) {
			assertTrue(e instanceof TaskInvalidIdException);
		}

		// editing floating task
		tasks.addToList("lowercase");
		tasks.editTaskDescriptionOnly(1, "UPPERCASE");
		assertEquals("UPPERCASE", tasks.getTask(0).getDescription());

		tasks.clearList();
		// editing floating task
		Date dateA = new Date(100);
		Date dateB = new Date(200);
		Date dateC = new Date(300);
		tasks.addToList("lowercase", dateA, dateB);
		tasks.editTaskDeadlineOnly(1, dateC);
		assertEquals(dateC, tasks.getTask(0).getDeadline());

		// add tasks to edit
		tasks.clearList();
		tasks = addTasks();

		// edit task descriptions
		assertEquals(tasks.getTask(0).getDescription(), "repeated task one");
		assertEquals(tasks.getTask(1).getDescription(), "deadline task one");
		assertEquals(tasks.getTask(2).getDescription(), "fixed task one");
		assertEquals(tasks.getTask(3).getDescription(), "floating task one");

		tasks.editTaskDescriptionOnly(1, "repeated task");
		tasks.editTaskDescriptionOnly(2, "deadline task");
		tasks.editTaskDescriptionOnly(3, "fixed task");
		tasks.editTaskDescriptionOnly(4, "floating task");

		assertEquals(tasks.getTask(0).getDescription(), "repeated task");
		assertEquals(tasks.getTask(1).getDescription(), "deadline task");
		assertEquals(tasks.getTask(2).getDescription(), "fixed task");
		assertEquals(tasks.getTask(3).getDescription(), "floating task");

		// edit task deadline (order: repeated, deadline, fixed)
		assertEquals(tasks.getTask(0).getDeadline(), yesterday);
		assertEquals(tasks.getTask(1).getDeadline(), today);
		assertEquals(tasks.getTask(2).getDeadline(), tomorrow);

		cal.add(Calendar.DAY_OF_MONTH, 3);
		Date daysAfter2 = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date daysAfter3 = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date daysAfter4 = cal.getTime();

		tasks.editTaskDeadlineOnly(3, daysAfter4);
		tasks.editTaskDeadlineOnly(2, daysAfter3);
		tasks.editTaskDeadlineOnly(1, daysAfter2);

		assertEquals(tasks.getTask(0).getDeadline(), daysAfter2);
		assertEquals(tasks.getTask(1).getDeadline(), daysAfter3);
		assertEquals(tasks.getTask(2).getDeadline(), daysAfter4);

		// edit fixed task dates: after edit, repeated, fixed, deadline
		assertEquals(((FixedTask) tasks.getTask(2)).getStartTime(), today);
		tasks.editTaskTimes(3, daysAfter2, daysAfter3);

		assertEquals(((FixedTask) tasks.getTask(1)).getStartTime(), daysAfter2);
		assertEquals(((FixedTask) tasks.getTask(1)).getDeadline(), daysAfter3);

	}

	@Test
	public void testMarkingDoneTask() throws TaskInvalidIdException,
			TaskDoneException {
		TaskList tasks = new TaskList();
		List<Integer> toDone = new ArrayList<Integer>();
		toDone.add(1);
		/* Testing invalid index */
		try {
			tasks.markTaskDone(toDone);
		} catch (TaskInvalidIdException e) {
			assertTrue(e instanceof TaskInvalidIdException);
		} catch (TaskDoneException e) {

		}

		/* Testing done single task */
		for (int i = 0; i < 10; i++) {
			tasks.addToList("No." + i);
			pause(10);
		}
		toDone = new ArrayList<Integer>();
		toDone.add(1);
		tasks.markTaskDone(toDone);
		assertTrue(tasks.getFinishedTasks().get(0).getIsDone());
		
		/* Testing done multiple tasks */
		tasks = new TaskList();
		toDone = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			tasks.addToList("No." + i);
			pause(10);
		}
		for (int i = 2; i < 8; i++) {
			toDone.add(i);
		}
		tasks.markTaskDone(toDone);
		List<Task> finished = tasks.getFinishedTasks();
		for (int i = 0; i < 5; i++) {
			assertTrue(finished.get(i).getIsDone());
		}
		tasks.setNotShowingDone();

	}

	@Test
	public void testTagging() {
	    
	    TaskList tasks = new TaskList();
	    tasks.addToList("floating one");
	    tasks.addToList("floating two");
	    tasks.addToList("floating three");
	    
	    /* tests for adding a tag */
	    // test tagging an invalid tag (null input)
	    try {
            tasks.tagTask(1, null);
            assert false;

        } catch (TaskTagDuplicateException e) {
            assert false;
        } catch (NullPointerException e) {
            assert true;
        }
	    
	    /* invalid empty input not tested since there would be an assertion error */
	    
	    // test tagging a valid tag
	    try {
	        tasks.tagTask(1, "tagg");
	        
	    } catch (TaskInvalidIdException e) {
	        assert false;
	    } catch (TaskTagDuplicateException e) {
	        assert false;
	    }
	    
	    // test invalid tagging of the same tag
	    try {
	        tasks.tagTask(1, "tagg");
	        assert tasks.getTask(0).getTags().contains("tagg");
	        
	    } catch (TaskInvalidIdException e) {
	        assert false;
	    } catch (TaskTagDuplicateException e) {
	        assert true;
	    }
	    
	    // test tagging a task >1 time
	    try {
	        tasks.tagTask(1, "tag again");
	        assert tasks.getTask(0).getTags().contains("tag again");
	        
        } catch (TaskInvalidIdException e) {
            assert false;
        } catch (TaskTagDuplicateException e) {
            assert false;
        }
	    
	    
	    /* tests for removing a tag */
	    // test untagging of invalid input (no such tag)
	    try {
	        tasks.untagTask(1, "la");
	        assert false;
	        
	    } catch (TaskInvalidIdException e) {
	        assert false;
	    } catch (TaskTagException e) {
	        assert true;
	    }
	    
	    // test untagging of valid input 
	    try {
	        tasks.untagTask(1, "tagg");
	        assert !tasks.getTask(0).getTags().contains("tagg");
	        
	    } catch (TaskInvalidIdException e) {
	        assert false;
	    } catch (TaskTagException e) {
	        assert false;
	    }

	}
	
	@Test
	public void testPrepareDisplayList() throws TaskInvalidDateException {

		TaskList tasks = new TaskList();
		tasks.addToList("Late", new Date(100), new Date(1000));
		pause(200); // wait for 0.2s to add the next one
		tasks.addToList("Early", new Date(200));
		pause(200); // wait for 0.2s to add the next one
		tasks.addToList("Middle", new Date(500));

		// Test order by addedTime
		List<Task> result = tasks.prepareDisplayList(true);

		assertEquals("Late", result.get(0).getDescription());
		assertEquals("Early", result.get(1).getDescription());
		assertEquals("Middle", result.get(2).getDescription());

		// Test order by addedTime
		result = tasks.prepareDisplayList(false);
		assertEquals("Early", result.get(0).getDescription());
		assertEquals("Middle", result.get(1).getDescription());
		assertEquals("Late", result.get(2).getDescription());
	}

	@Test
	public void testUndoRedo() throws TaskInvalidIdException, TaskInvalidDateException {
	    TaskList tasks = new TaskList();
	    tasks.clearUndoRedoStack();
	    
	    // testing for invalid undo
	    try {
            tasks.undo();
            assert false;
        } catch (UndoException e) {
            assert true;
        }
	    
	    // testing for invalid redo
	    try {
	        tasks.redo();
	        assert false;
        } catch (RedoException e) {
            assert true;
        }
	    
	    try {
	        TaskList controlList = addTasks();
	        
	        // test undo/redo for adding
	        tasks = addTasks();
	        assert (tasks.count() == 4);
	        
	        tasks.undo();
	        tasks.undo();
	        tasks.undo();
	        tasks.undo();
	        assert (tasks.count() == 0);

	        tasks.redo();
	        tasks.redo();
	        tasks.redo();
	        tasks.redo();
	        assert (tasks.count() == 4);
	        
	        
	        // test undo/redo for editing
	        tasks.editTaskDescriptionOnly(4, "task one");
	        tasks.editTaskDeadlineOnly(2, tomorrow);
	        tasks.editTaskDescriptionDeadline(1, "task two", today);
	        tasks.editTaskDescriptionTimes(3, "task three", yesterday, today);
            assert (!tasks.isEqual(controlList));
            
            tasks.undo();
            tasks.undo();
            tasks.undo();
            tasks.undo();
            assert (tasks.isEqual(controlList));

            tasks.redo();
            tasks.redo();
            tasks.redo();
            tasks.redo();
            
            assertEquals(tasks.getTask(0).getDescription(), "task three");
            assertEquals(((FixedTask) tasks.getTask(0)).getStartTime(), yesterday);
            assertEquals(tasks.getTask(1).getDeadline(), today);
            assertEquals(tasks.getTask(2).getDeadline(), tomorrow);
            
            
            // test undo/redo for deleting tasks
            tasks = addTasks();
            List<Integer> taskIndexList = new ArrayList<Integer>();
            taskIndexList.add(2);
            taskIndexList.add(4);
            tasks.deleteFromList(taskIndexList);
            assert (tasks.count() == 2);
            
            tasks.undo();
            assert (tasks.count() == 4);
            
            tasks.redo();
            assert (tasks.count() == 2);
            
            tasks.clearList();
            assert (tasks.count() == 0);

            tasks.undo();
            assert (tasks.count() == 2);
            
            tasks.redo();
            assert (tasks.count() == 0);

            
            // test undo/redo for marking tasks done
            tasks = addTasks();
            taskIndexList.add(1);
            taskIndexList.add(3);
            tasks.markTaskDone(taskIndexList);
            assert (tasks.countFinished() == 4);
            assert (tasks.count() == 1);
            
            tasks.undo();
            assert (tasks.countFinished() == 0);
            assert (tasks.count() == 4);
            
            tasks.redo();
            assert (tasks.countFinished() == 4);
            assert (tasks.count() == 1);
            
	    } catch (UndoException e) {
	        assert false;
	    } catch (RedoException e) {
	        assert false;
	    } catch (TaskDoneException e) {
	        assert false;
        }
	}
	
	void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 
	 * @return tasklist for the four tasks, in the order: 1.Repeated Tasks
	 *         2.Deadline Tasks 3.Fixed Task 4.Floating Tasks
	 */
	private TaskList addTasks() {
		TaskList tasks = new TaskList();

		// add a floating task
		tasks.addToList("floating task one");

		// add a deadline task
		tasks.addToList("deadline task one", today);

		// add a fixed task
		try {
			tasks.addToList("fixed task one", today, tomorrow);
		} catch (TaskInvalidDateException e) {
			assert false;
		}

		// add a repeated task
		tasks.addToList("repeated task one", yesterday,
				UserInput.RepeatDate.WEEKLY);

		assertEquals(tasks.count(), 4);
		return tasks;
	}

	@BeforeClass
	public static void setDates() {
		today = new Date();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		tomorrow = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, -2);
		yesterday = cal.getTime();
	}

}
