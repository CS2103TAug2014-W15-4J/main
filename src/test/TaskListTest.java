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
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;

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
			tasks.editTaskDescription(0, "UPPERCASE");
		} catch (TaskInvalidIdException e) {
			assertTrue(e instanceof TaskInvalidIdException);
		}

		// editing floating task
		tasks.addToList("lowercase");
		tasks.editTaskDescription(1, "UPPERCASE");
		assertEquals("UPPERCASE", tasks.getTask(0).getDescription());

		tasks.clearList();
		// editing floating task
		Date dateA = new Date(100);
		Date dateB = new Date(200);
		Date dateC = new Date(300);
		tasks.addToList("lowercase", dateA, dateB);
		tasks.editTaskDeadline(1, dateC);
		assertEquals(dateC, tasks.getTask(0).getDeadline());

		// add tasks to edit
		tasks.clearList();
		tasks = addTasks();

		// edit task descriptions
		assertEquals(tasks.getTask(0).getDescription(), "repeated task one");
		assertEquals(tasks.getTask(1).getDescription(), "deadline task one");
		assertEquals(tasks.getTask(2).getDescription(), "fixed task one");
		assertEquals(tasks.getTask(3).getDescription(), "floating task one");

		tasks.editTaskDescription(1, "repeated task");
		tasks.editTaskDescription(2, "deadline task");
		tasks.editTaskDescription(3, "fixed task");
		tasks.editTaskDescription(4, "floating task");

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

		tasks.editTaskDeadline(3, daysAfter4);
		tasks.editTaskDeadline(2, daysAfter3);
		tasks.editTaskDeadline(1, daysAfter2);

		assertEquals(tasks.getTask(0).getDeadline(), daysAfter2);
		assertEquals(tasks.getTask(1).getDeadline(), daysAfter3);
		assertEquals(tasks.getTask(2).getDeadline(), daysAfter4);

		System.out.println(tasks.getTask(0).getDescription());
		System.out.println(tasks.getTask(1).getDescription());
		System.out.println(tasks.getTask(2).getDescription());

		// edit fixed task start date
		assertEquals(((FixedTask) tasks.getTask(2)).getStartTime(), today);
		tasks.editTaskStartDate(3, daysAfter2);
		assertEquals(((FixedTask) tasks.getTask(2)).getStartTime(), daysAfter2);
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
			toDone.add(3);
		}
		tasks.markTaskDone(toDone);
		List<Task> finished = tasks.getFinishedTasks();
		for (int i = 0; i < 5; i++) {
			assertTrue(finished.get(i).getIsDone());
		}
		tasks.setNotShowingDone();

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
