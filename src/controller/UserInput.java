package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 
 * Object passing from parser to logic The format of time is "yyyy-MM-dd HH:mm"
 * (For command with only one time, default we use addBeginDate and
 * getBeginDate) (SpecialDates are informal description of time like today,
 * tomorrow, Monday and all in lowercase)
 * 
 * @author Lu Yuehan
 *
 */
public class UserInput {
	private boolean valid = true;
	private String command = null;
	
	private boolean isExit = false;

	private boolean isAdd = false;

	private boolean isDelete = false;
	
	private List<Integer> ID = new ArrayList<Integer>();

	private boolean isEdit = false;
	private int editID = 0;
	private String editCommand = null;

	private boolean isShow = false;

	private boolean isClear = false;

	private boolean isDone = false;

	private String description = null;

	private boolean floating = false;

	private Date beginTime;
	private String specialBeginDate;
	private Date endTime;
	private String specialEndDate;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public boolean getValid() {
		return valid;
	}

	public String getCommand() {
		return command;
	}
	
	public boolean isExit() {
		return isExit;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public boolean isDone() {
		return isDone;
	}

	public List<Integer> getDeleteID() {
		return ID;
	}

	public List<Integer> getDoneID() {
		return ID;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public int getEditID() {
		return editID;
	}

	public String getEditCommand() {
		return editCommand;
	}

	public boolean isShow() {
		return isShow;
	}

	public boolean isClear() {
		return isClear;
	}

	public String getDescription() {
		return description;
	}

	public boolean isFloat() {
		return floating;
	}

	public Date getBeginDate() {
		return beginTime;
	}

	public String getSpecialBeginDate() {
		return specialBeginDate;
	}

	public Date getEndDate() {
		return endTime;
	}

	public void beDone() {
		isDone = true;
	}
	
	public void beExit() {
		isExit = true;
	}

	public String getSpecialEndDate() {
		return specialEndDate;
	}

	public void unvalidation() {
		valid = false;
	}

	public void add(String userCommand, String userDescription,
			boolean userFloat) {
		command = userCommand;
		description = userDescription;
		floating = userFloat;
	}

	public void beAdd() {
		isAdd = true;
	}

	public void beDelete() {
		isDelete = true;
	}

	public void beEdit() {
		isEdit = true;
	}

	public void beShow() {
		isShow = true;
	}

	public void beClear() {
		isClear = true;
	}

	public void addDeleteID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addDoneID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addEdit(int number, String command) {
		editID = number;
		editCommand = command;
	}

	public void addBeginDate(String beginDate) throws ParseException {
		beginTime = timeFormat.parse(beginDate);
	}

	public void addSpecialBeginDate(String date) {
		specialBeginDate = date;
	}

	public void addEndDate(String endDate) throws ParseException {
		endTime = timeFormat.parse(endDate);
	}

	public void addSpecialEndDate(String date) {
		specialEndDate = date;
	}

}
