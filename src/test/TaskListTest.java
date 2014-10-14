package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.FixedTask;
import model.FloatingTask;
import model.TaskList;

import org.junit.BeforeClass;
import org.junit.Test;

public class TaskListTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testAddingTask() {
		TaskList tasks = new TaskList();
		// generate 50 dummy task and add them
		 for (int i = 0; i < 50; i++) {
	            tasks.addToList(new FloatingTask("No." + i));
		}
		assertEquals(50, tasks.count());
	}
	
	@Test
	public void testDeletingTask() {
		TaskList tasks = new TaskList();
		// generate 50 dummy task and add them
		 for (int i = 0; i < 50; i++) {
	            tasks.addToList(new FloatingTask("No." + i));
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
	
	public void testEditingTask() {
		TaskList tasks = new TaskList();
		// editing floating task
		FloatingTask floatTask = new FloatingTask("lowercase");
		tasks.addToList(floatTask);
		tasks.editTaskDescription(1, "UPPERCASE");
		assertEquals("UPPERCASE", tasks.get(0).getDescription());
		
		tasks.clearList();
		// editing floating task
		Date dateA = new Date(100);
		Date dateB = new Date(200);
		Date dateC = new Date(300);
		FixedTask fixedTask = new FixedTask("lowercase", dateA, dateB);
		tasks.addToList(fixedTask);
		tasks.editTaskStartDate(1, dateC);
		assertEquals(dateC, tasks.get(0).getDeadline());
	}

}
