package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import model.FixedTask;
import model.FloatingTask;
import model.Task;
import model.TaskList;

import org.junit.BeforeClass;
import org.junit.Test;

import exception.TaskInvalidDateException;

public class TaskListTest {
	@Test
	public void testAddingTask() {
		TaskList tasks = new TaskList();
		// generate 50 dummy task and add them
		 for (int i = 0; i < 50; i++) {
	            tasks.addToList("No." + i);
		}
		assertEquals(50, tasks.count());
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
		
		//delete multiple task
		toDelete = new ArrayList<Integer>();
		for (int i = 1; i <=25; i++) {
            toDelete.add(i);
		}
		try {
			tasks.deleteFromList(toDelete);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(24, tasks.count());
		
		// clear tasks
		tasks.clearList();
		assertEquals(0, tasks.count());
	}
	
	@Test
	public void testEditingTask() throws TaskInvalidDateException {
		TaskList tasks = new TaskList();
		// editing floating task
		tasks.addToList("lowercase");
		tasks.editTaskDescription(1, "UPPERCASE");
		assertEquals("UPPERCASE", tasks.get(0).getDescription());
		
		tasks.clearList();
		// editing floating task
		Date dateA = new Date(100);
		Date dateB = new Date(200);
		Date dateC = new Date(300);
		tasks.addToList("lowercase", dateA, dateB);
		tasks.editTaskStartDate(1, dateC);
		assertEquals(dateC, tasks.get(0).getDeadline());
	}
	
	@Test
	public void testPrepareDisplayList() {
		
		
		TaskList tasks = new TaskList();
		tasks.addToList("Late", new Date(100), new Date(1000));
		pause(200); // wait for 0.2s to add the next one
		tasks.addToList("Early", new Date(200));
		pause(200); // wait for 0.2s to add the next one
		tasks.addToList("Middle", new Date(500));
		
		// Test order by addedTime
		Queue<Task> result = tasks.prepareDisplayList(true);
		assertEquals("Late", result.poll().getDescription());
		assertEquals("Early", result.poll().getDescription());
		assertEquals("Middle", result.poll().getDescription());
		
		// Test order by addedTime
		result = tasks.prepareDisplayList(false);
		assertEquals("Early", result.poll().getDescription());
		assertEquals("Middle", result.poll().getDescription());
		assertEquals("Late", result.poll().getDescription());
		
	}
	
	void pause(int ms) {
		try {
		    Thread.sleep(ms); 
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}

}
