package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

import model.Task;
import model.TaskList;

public class Storage {
	
	private static final String TASK_FILE = "uClear.xml";
	
	private static final String ERROR_IO = "Error when trying to read/write the file.";
	
	// These two Reader and Writer are used to access and manipulate the given
	// text file
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private ArrayList<String> tasks;
	
	public Storage() {
		this.initilize();
	}
	
	public String save(ArrayList<Task> list) {
		for (Task t: list) {
			this.tasks.add(serialize(t));
		}
		
		writeToFile(tasks);
		
		return "Success";
	}
	
	public TaskList load() {
		return null;
	}
	
	public String serialize(Task task) {
		XStream xstream = new XStream();
		xstream.alias("task", Task.class);
		String xml = xstream.toXML(task);
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
	private void initilize() {
		this.tasks = new ArrayList<>();
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
	}
	
	
	private void readFromFile() {
		
	}
	
	private void writeToFile(ArrayList<String> tasks) {
		try {
			this.writer = new BufferedWriter(new FileWriter(TASK_FILE, false));
		} catch (IOException e1) {
			throw new Error(ERROR_IO);
		}
		
		for (String task : tasks) {
			try {
				this.writer.write(task);
			} catch (Exception e) {
				throw new Error(ERROR_IO);
			}
		}
	}
	
	public static void main(String[] args) {
		ArrayList<Task> test_input= new ArrayList<>();
		test_input.add(new Task("IamHappy"));
		test_input.add(new Task("Macbook"));
		test_input.add(new Task("go to school tonight"));
		System.out.println("aa");
		Storage storge = new Storage();
		storge.save(test_input);
		storge.close();
	}
}
