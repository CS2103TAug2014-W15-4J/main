package model;

public class FloatingTask extends Task {

	public FloatingTask(String description) {
		super(description);
		this.taskType = Type.FLOAT;
	}

	@Override
	public String toString() {
		return this.description;
	}
	
	

}
