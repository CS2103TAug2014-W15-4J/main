package test;

import static org.junit.Assert.*;

import java.util.Date;

import model.DeadlineTask;
import model.FixedTask;
import model.FloatingTask;
import model.TaskList;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Storage;
import exception.TaskInvalidDateException;

public class StorageTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testEmpty() {
		Storage store = new Storage("test.xml");
		TaskList tasks = new TaskList();
		store.save(tasks, "test.xml");
		TaskList listFromFile = store.load();
		assertEquals(0, listFromFile.count());
	}
	
	@Test
	public void testMultipleTypesTask() throws TaskInvalidDateException {
		Storage store = new Storage("test.xml");
		TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
			tasks.addToList("No. "+i);
		}
		store.save(tasks, "test.xml");
		
		TaskList listFromFile = store.load();
		assertEquals(100, listFromFile.count());
		store.close();
		
		store = new Storage("test.xml");
        for (int i = 0; i < 50; i++) {
            tasks.addToList("test" + i, new Date(10), new Date(100));
		}
		store.save(tasks, "test.xml");
		listFromFile = store.load();
		assertEquals(150, listFromFile.count());
		
		store.close();
		
		store = new Storage("test.xml");
        for (int i = 0; i < 60; i++) {
            tasks.addToList("test" + i, new Date());
		}
        store.save(tasks, "test.xml");
		listFromFile = store.load();
		assertEquals(210, listFromFile.count());
	}

}
