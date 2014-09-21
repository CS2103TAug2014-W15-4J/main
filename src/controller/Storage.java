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

public class Storage {
	
	private static final String TASK_FILE = "uClear.xml";
	
	private static final String ERROR_IO = "Error when trying to read/write the file.";
	
	// These two Reader and Writer are used to access and manipulate the given
	// text file
	private BufferedReader reader;
	private BufferedWriter writer;
	
	public Storage() {
		this.initilize();
	}
	
	public String save(TaskList list) {
		return null;
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
	
	private  void saveToFile() {
		
	}
	
	public static void main(String[] args) {
		Task a = new Task("going to movies tonight");
		Storage storge = new Storage();
		System.out.println(storge.serialize(a));
		storge.close();
	}
}
