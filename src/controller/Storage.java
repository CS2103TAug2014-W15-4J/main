package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import log.ULogger;
import model.DeadlineTask;
import model.FixedTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.SortedArrayList;
import model.Task;
import model.TaskList;

//@author A0119446B
/**
 * This Storage class is for saving tasks to local file, 
 * and loading tasks from local file.
 * 
 * @author Jiang Sheng
 *
 */
public class Storage {
	
	
	private static final String ALIAS_CLASS_LIST = "list";
	private static final String ALIAS_CLASS_MAP = "map";
	// XML tag for Model Class 
	private static final String ALIAS_CLASS_FLOATING_TASK = "FloatingTask";
	private static final String ALIAS_CLASS_FIXED_TASK = "FixedTask";
	private static final String ALIAS_CLASS_DEADLINE_TASK = "DeadlineTask";
	private static final String ALIAS_CLASS_REPEATED_TASK = "RepeatedTask";
	private static final String ALIAS_CLASS_TASKLIST = "CompleteList";
	private static final String ALIAS_CLASS_SORTED_LIST = "SortedList";


	private static final String TASK_FILE = "uClear.xml";
	private static final String MESSAGE_SUCCESS = "Success";
	private static final String ERROR_IO = "Error when trying to read/write the file.";
	
	private static ULogger logger = ULogger.getLogger();
	
	// These two Reader and Writer are used to access and manipulate the given
	// text file
	private BufferedReader reader;
	private BufferedWriter writer;
	private XStream xstream;
	
	private String file_name;
	/**
	 * Constructor
	 * 
	 */
	public Storage() {
		this.file_name = TASK_FILE;
		this.initilize(file_name);
	}
	
	public Storage(String filename) {
		this.file_name = filename;
		this.initilize(file_name);
	}
	
	/**
	 * Save a TaskList object into the data file.
	 * @param tasks the TaskList object which contains the list of tasks
	 * @return a feedback message
	 */
	public String save(TaskList tasks) {
		logger.info("Going to save tasks on hard disk");
		try {
			this.writer = new BufferedWriter(new FileWriter(this.file_name, false));
			this.writer.write(serialize(tasks));
			this.writer.close();
		} catch (IOException e) {
			logger.error("I/O error when saving tasks.");
			throw new Error(ERROR_IO);
		}
		logger.info("Saving complete.");
		return MESSAGE_SUCCESS;
	}
	
	/**
	 * Overload
	 * @param tasks the TaskList object which contains the list of tasks\
	 * @param filename the name of the task list to be stored
	 * @return a feedback message
	 */
	public String save(TaskList tasks, String filename) {
		logger.info("Going to save tasks on hard disk");
		try {
			this.writer = new BufferedWriter(new FileWriter(filename, false));
			this.writer.write(serialize(tasks));
			this.writer.close();
		} catch (IOException e) {
			logger.error("I/O error when saving tasks.");
			throw new Error(ERROR_IO);
		}
		logger.info("Saving complete.");
		return MESSAGE_SUCCESS;
	}
	
	/**
	 * Load and build a TaskList object from the data file.
	 * @return a TaskList object
	 */
	public TaskList load() {
		logger.info("Going to load tasks from data file.");
		String input = null;
		StringBuilder xml = new StringBuilder(); 
		try {
			while ((input = reader.readLine()) != null) {
				xml.append(input);
			}
		} catch (IOException e) {
			logger.error("I/O error when loading tasks.");
			throw new Error(ERROR_IO);
		}
		logger.info("Loading completed.");
		return (TaskList)xstream.fromXML(xml.toString());
	}
	
	public void close() {
		try {
			this.reader.close();
			this.writer.close();
		} catch (IOException e) {
			throw new Error(ERROR_IO);
		}
		
	}
	
	/**
	 * Export user's list of task into txt file.
	 * @param tasks the TaskList object which contains the list of tasks
	 * @return a feedback message
	 */
	public static void export(List<Task> taskTImed, List<Task> taskTodo, List<Task> taskFinished) {
		logger.info("Going to export task list to tasklist.txt file");
		// preparation work
		StringBuilder output = new StringBuilder();
		output.append("Thank you for using uClear\n");
		output.append("Here are your current tasks.\n\n");
		output.append("Tasks that due soon:\n");
		for (Task task : taskTImed) {
			output.append(task.toString());
			output.append("\n\n");
		}
		output.append("Tasks to do:\n");
		for (Task task : taskTodo) {
			output.append(task.toString());
			output.append("\n\n");
		}
		output.append("Tasks finished:\n");
		for (Task task : taskFinished) {
			output.append(task.toString());
			output.append("\n\n");
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("tasklist.txt", false));
			writer.write(output.toString());
			writer.close();
		} catch (IOException e) {
			logger.error("I/O error when exporting tasks.");
			throw new Error(ERROR_IO);
		}
		logger.info("Exporting complete.");
	}

	/**
	 * Serialize a TaskList object into xml string
	 * @return xml format string
	 */
	private String serialize(TaskList tasks) {
		String xml = xstream.toXML(tasks);
		return xml;
	}

	/**
	 * This method initialize all the file operators
	 * @param filename the name of the task list to be stored
	 */
	private void initilize(String filename) {
		
		this.xstream = new XStream();
		this.xstream.alias(ALIAS_CLASS_TASKLIST, TaskList.class);
		this.xstream.alias(ALIAS_CLASS_FLOATING_TASK, FloatingTask.class);		
		this.xstream.alias(ALIAS_CLASS_FIXED_TASK, FixedTask.class);
		this.xstream.alias(ALIAS_CLASS_DEADLINE_TASK, DeadlineTask.class);
		this.xstream.alias(ALIAS_CLASS_REPEATED_TASK, RepeatedTask.class);
		this.xstream.alias(ALIAS_CLASS_SORTED_LIST, SortedArrayList.class);
		this.xstream.alias(ALIAS_CLASS_MAP, java.util.Map.class);
		this.xstream.alias(ALIAS_CLASS_LIST, java.util.ArrayList.class);
		
		this.xstream.processAnnotations(TaskList.class);
		this.xstream.processAnnotations(FloatingTask.class);
		this.xstream.processAnnotations(FixedTask.class);
		this.xstream.processAnnotations(DeadlineTask.class);
		this.xstream.processAnnotations(RepeatedTask.class);
		this.xstream.processAnnotations(SortedArrayList.class);

		File inputFile = new File(filename);
		try {
			if (inputFile.exists()) {
				this.reader = new BufferedReader(new FileReader(inputFile));
			} else {
				// if the file does not exist, we create a new file
				this.writer = new BufferedWriter(new FileWriter(inputFile, false));
				this.reader = new BufferedReader(new FileReader(inputFile));
				
				// write an empty list to it.
				TaskList empty = new TaskList();
				try {
					this.writer.write(serialize(empty));
					this.writer.close();
				} catch (IOException e) {
					throw new Error(ERROR_IO);
				}
			}
		} catch (Exception e) {
			throw new Error(ERROR_IO);
		}	
	}
}
