package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import model.DeadlineTask;
import model.FixedTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.Task.Type;
import controller.UserInput.RepeatDate;

import org.junit.BeforeClass;
import org.junit.Test;

import exception.TaskDoneException;
import exception.TaskTagDuplicateException;

public class TaskTest {
	static Date today;
	static Date tomorrow;
	static Date theDayAfterTomorrow;
	static Date nextWeekOfTomorrow;
	static Date nextMonthOfTomorrow;
	
	@BeforeClass
	public static void setUpTime() {
		today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, 1);
		tomorrow = cal.getTime();
		cal.add(Calendar.DATE, 1);
		theDayAfterTomorrow = cal.getTime();
		
		cal.setTime(tomorrow);
		cal.add(Calendar.DATE, 7);
		nextWeekOfTomorrow = cal.getTime();
		cal.setTime(tomorrow);
		cal.add(Calendar.MONTH, 1);
		nextMonthOfTomorrow = cal.getTime();
	}
	
	
	@Test
	public void testFloatingTask() throws TaskTagDuplicateException {
		FloatingTask task = new FloatingTask("This is a floating task");
		assertEquals(Type.FLOAT ,task.getType());
		assertEquals("This is a floating task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		// test tagging
		// add tag
		task.addTag("Tag1");
		task.addTag("Tag2");
		task.addTag("Tag3");
		
		// test existing tags
		assertTrue(task.getTags().contains("tag1"));
		assertTrue(task.getTags().contains("tag2"));
		assertTrue(task.getTags().contains("tag3"));
		
		// test non-existing tags
		assertFalse(task.getTags().contains("tag4"));
		assertFalse(task.getTags().contains("tag5"));
		
		// test adding same tag
		try {
			task.addTag("Tag1");
		} catch (TaskTagDuplicateException e) {
			assertTrue(e instanceof TaskTagDuplicateException);
		}
	}

	@Test
	public void testFixedTask() throws TaskTagDuplicateException {
		FixedTask task = new FixedTask("This is a Fixed task", today, tomorrow);
		assertEquals(Type.FIXED ,task.getType());
		assertEquals("This is a Fixed task", task.getDescription());
		assertFalse(task.getIsDone());
				
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(today, task.getStartTime());
		assertEquals(tomorrow, task.getDeadline());
		
		// test remaining days
		assertEquals(1, task.getReminingDays());
		
		task.setStartTime(tomorrow);
		assertEquals(tomorrow, task.getStartTime());
		task.setDeadline(theDayAfterTomorrow);
		assertEquals(theDayAfterTomorrow, task.getDeadline());
		
		
		// test remaining days
		assertEquals(2, task.getReminingDays());
		
		// test tagging
		// add tag
		task.addTag("Tag1");
		task.addTag("Tag2");
		task.addTag("Tag3");
		
		// test existing tags
		assertTrue(task.getTags().contains("tag1"));
		assertTrue(task.getTags().contains("tag2"));
		assertTrue(task.getTags().contains("tag3"));
		
		// test non-existing tags
		assertFalse(task.getTags().contains("tag4"));
		assertFalse(task.getTags().contains("tag5"));
		
		// test adding same tag
		try {
			task.addTag("Tag1");
		} catch (TaskTagDuplicateException e) {
			assertTrue(e instanceof TaskTagDuplicateException);
		}
	}
	
	@Test
	public void testDeadlineTask() throws TaskTagDuplicateException {
		DeadlineTask task = new DeadlineTask("This is a Deadline task", tomorrow);
		assertEquals(Type.DEADLINE ,task.getType());
		assertEquals("This is a Deadline task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(tomorrow, task.getDeadline());
		
		// test remaining days
		assertEquals(1, task.getReminingDays());
		
		// test edit time
		task.setDeadline(theDayAfterTomorrow);
		assertEquals(theDayAfterTomorrow, task.getDeadline());
		
		// test remaining days after changing time
		assertEquals(2, task.getReminingDays());
				
		// test tagging
		// add tag
		task.addTag("Tag1");
		task.addTag("Tag2");
		task.addTag("Tag3");
		
		// test existing tags
		assertTrue(task.getTags().contains("tag1"));
		assertTrue(task.getTags().contains("tag2"));
		assertTrue(task.getTags().contains("tag3"));
		
		// test non-existing tags
		assertFalse(task.getTags().contains("tag4"));
		assertFalse(task.getTags().contains("tag5"));
		
		// test adding same tag
		try {
			task.addTag("Tag1");
		} catch (TaskTagDuplicateException e) {
			assertTrue(e instanceof TaskTagDuplicateException);
		}
	}
	
	@Test
	public void testRepeatedTask() throws TaskDoneException {	
		// DAILY task
		
		// set deadline to be tomorrow
		RepeatedTask task = new RepeatedTask("This is a Repeated task", tomorrow, RepeatDate.DAILY);
		assertEquals(Type.REPEATED ,task.getType());
		assertEquals("This is a Repeated task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(tomorrow, task.getDeadline());
		
		// test remaining days
		assertEquals(1, task.getReminingDays());
		
		// check occurrence
		
		// done this task, check if it return a new task for the next repeat period
		RepeatedTask nextTask = (RepeatedTask) task.markDone();
		assertEquals(theDayAfterTomorrow, nextTask.getDeadline());
		
		// Weekly task
		
		// set deadline to be tomorrow
		task = new RepeatedTask("This is a Repeated task", tomorrow, RepeatDate.WEEKLY);
		assertEquals(Type.REPEATED ,task.getType());
		assertEquals("This is a Repeated task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(tomorrow, task.getDeadline());
		
		// test remaining days
		assertEquals(1, task.getReminingDays());
		
		// check occurrence
		
		// done this task, check if it return a new task for the next repeat period
		nextTask = (RepeatedTask) task.markDone();
		assertEquals(nextWeekOfTomorrow, nextTask.getDeadline());
		
		// Weekly task
		
		// set deadline to be tomorrow
		task = new RepeatedTask("This is a Repeated task", tomorrow, RepeatDate.MONTHLY);
		assertEquals(Type.REPEATED ,task.getType());
		assertEquals("This is a Repeated task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(tomorrow, task.getDeadline());
		
		// test remaining days
		assertEquals(1, task.getReminingDays());
		
		// check occurrence
		
		// done this task, check if it return a new task for the next repeat period
		nextTask = (RepeatedTask) task.markDone();
		assertEquals(nextMonthOfTomorrow, nextTask.getDeadline());
		
		
	}

}
