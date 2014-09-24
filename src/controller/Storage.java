package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import model.Task;
import model.TaskList;


/**
 * This Storage class is for saving tasks to local file, 
 * and loading tasks from local file.
 * 
 * @author Jiang Sheng
 *
 */
public class Storage {
	
	private static final String TASK_FILE = "uClear.xml";
	
	private static final String ERROR_IO = "Error when trying to read/write the file.";
	
	// These two Reader and Writer are used to access and manipulate the given
	// text file
	private BufferedReader reader;
	private BufferedWriter writer;
	private XStream xstream;
	
	/**
	 * Constructor
	 * 
	 * 
	 */
	public Storage() {
		this.initilize();
	}
	
	/**
	 * Save an TaskList object into the data file.
	 * @param tasks the TaskList object which contains the list of tasks
	 * @return a feedback message
	 */
	public String save(TaskList tasks) {
		try {
			this.writer = new BufferedWriter(new FileWriter(TASK_FILE, false));
			this.writer.write(serialize(tasks));
		} catch (IOException e) {
			throw new Error(ERROR_IO);
		}
		return "Success";
	}
	
	/**
	 * Load and build a TaskList object from the data file.
	 * @return a TaskList object
	 */
	public TaskList load() {
		String input = null;
		StringBuilder xml = new StringBuilder(); 
		try {
			while ((input = reader.readLine()) != null) {
				xml.append(input);
			}
		} catch (IOException e) {
			throw new Error(ERROR_IO);
		}
//		System.out.println(xml.toString());
		return (TaskList)xstream.fromXML(xml.toString());
	}
	
	/**
	 * Serialize a TaskList object into xml string
	 * @return xml format string
	 */
	public String serialize(TaskList tasks) {
		String xml = xstream.toXML(tasks);
		return xml;
	}
	
	private void close() {
		try {
			this.reader.close();
			this.writer.close();
		} catch (IOException e) {
			throw new Error(ERROR_IO);
		}
		
	}
	
	/**
	 * This method initialize all the file operators
	 */
	private void initilize() {
		File inputFile = new File(TASK_FILE);
		try {
			if (inputFile.exists()) {
				this.reader = new BufferedReader(new FileReader(inputFile));
				this.writer = new BufferedWriter(new FileWriter(inputFile, true));
			} else {
				// if the file does not exist, we create a new file
				this.writer = new BufferedWriter(new FileWriter(inputFile, false));
				this.reader = new BufferedReader(new FileReader(inputFile));
			}
		} catch (Exception e) {
			throw new Error(ERROR_IO);
		}	
		this.xstream = new XStream();
		this.xstream.alias("CompleteList", TaskList.class);
		this.xstream.alias("Task", Task.class);
		this.xstream.processAnnotations(TaskList.class);
	}
	
	
	public static void main(String[] args) {
		testSave();
		testLoad();
	}

	
	/*************
	 * Testing
	 *************/
	
	
	/**
	 * Test save method
	 */
	public static void testSave() {
		TaskList a = new TaskList();
		a.test();
		
		Storage storge = new Storage();
//		System.out.println(storge.serialize(a));
		storge.save(a);
		storge.close();
	}
	
	/**
	 * Test load method
	 */
	public static void testLoad() {
		Storage storge = new Storage();
		TaskList a = storge.load();
		System.out.println(a.toString());
	}
}
