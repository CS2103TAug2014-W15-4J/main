package test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import model.DeadlineTask;
import model.FixedTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.Task;
import model.Task.Type;

import controller.UserInput.RepeatDate;

import org.junit.Test;

import exception.TaskTagDuplicateException;

public class TaskTest {

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
		testTags(task);
	}

	@Test
	public void testFixedTask() throws TaskTagDuplicateException {
		Date start = new Date(100);
		Date end = new Date(200);
		FixedTask task = new FixedTask("This is a Fixed task", start, end);
		assertEquals(Type.FIXED ,task.getType());
		assertEquals("This is a Fixed task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(start, task.getStartTime());
		assertEquals(end, task.getDeadline());
		
		// test edit time
		Date newStart = new Date(100);
		Date newEnd = new Date(100);
		
		task.setStartTime(newStart);
		assertEquals(newStart, task.getStartTime());
		task.setDeadline(newEnd);
		assertEquals(newEnd, task.getDeadline());
		
		// test tagging
		testTags(task);
	}
	
	@Test
	public void testDeadlineTask() throws TaskTagDuplicateException {
		Date end = new Date(200);
		DeadlineTask task = new DeadlineTask("This is a Deadline task", end);
		assertEquals(Type.DEADLINE ,task.getType());
		assertEquals("This is a Deadline task", task.getDescription());
		assertFalse(task.getIsDone());
		
		// test edit description
		task.setDescription("Description edited");
		assertEquals("Description edited", task.getDescription());
		
		assertEquals(end, task.getDeadline());
		
		// test edit time
		Date newEnd = new Date(100);
		
		task.setDeadline(newEnd);
		assertEquals(newEnd, task.getDeadline());
		
		// test tagging
		testTags(task);
	}
	
	@Test
	public void testRepeatedTask() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();
		
		// DAILY task
		
		// set deadline to be tomorrow
		RepeatedTask task = new RepeatedTask("This is a Deadline task", tomorrow, RepeatDate.DAILY);
		assertEquals(Type.REPEATED ,task.getType());
		assertEquals("This is a Deadline task", task.getDescription());
		assertFalse(task.getIsDone());
		
		assertEquals(tomorrow, task.getDeadline());
	}

	/**
	 * @param task
	 * @throws TaskTagDuplicateException
	 */
	private void testTags(Task task) throws TaskTagDuplicateException {
		// add tag
		task.addTag("Tag1");
		task.addTag("Tag2");
		task.addTag("Tag3");
		task.addTag("Tag4");
		task.addTag("Tag5");
		
		// test existing tags
		assertTrue(task.getTags().contains("tag1"));
		assertTrue(task.getTags().contains("tag2"));
		assertTrue(task.getTags().contains("tag3"));
		assertTrue(task.getTags().contains("tag4"));
		assertTrue(task.getTags().contains("tag5"));
		
		// test non-existing tags
		assertFalse(task.getTags().contains("tag6"));
		assertFalse(task.getTags().contains("tag7"));
		assertFalse(task.getTags().contains("tag8"));
		
		// test adding same tag
		try {
			task.addTag("Tag1");
		} catch (TaskTagDuplicateException e) {
			assertTrue(e instanceof TaskTagDuplicateException);
		}
	}

}
