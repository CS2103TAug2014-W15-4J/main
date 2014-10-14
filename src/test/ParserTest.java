package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import controller.Parser;
import controller.UserInput;
import controller.UserInput.RepeatDate;

public class ParserTest {

	@Test
	public void taskTypeTest() {
		Parser parser = new Parser();
		String test1 = "add go to sleep";
		UserInput input = parser.parse(test1);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD,input.getCommand());
		assertTrue(input.isFloat());
		assertFalse(input.isDeadline());
		
		String test2 = "add go to sleep by tomorrow";
		input = parser.parse(test2);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD,input.getCommand());
		assertTrue(input.isDeadline());
		assertFalse(input.isFloat());
		
		String test3 = "add go to sleep every 2pm daily";
		input = parser.parse(test3);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD,input.getCommand());
		assertFalse(input.isDeadline());
		assertFalse(input.isFloat());
		assertTrue(input.isRepeated());
		assertEquals(input.repeatDate(),RepeatDate.DAILY);
		
		String test4 = "edit 2";
		input = parser.parse(test4);
		assertFalse(input.getValid());
		
		String test5 = "edit 2 going to school 11pm to 6pm";
		input = parser.parse(test5);
		assertTrue(input.getValid());
		assertEquals(2,input.getEditID());
		assertEquals(UserInput.CMD.EDIT,input.getCommand());
        assertEquals("going to school",input.getDescription());
		
		String test6 = "delete 1 2 3 4";
		input = parser.parse(test6);
		List<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.DELETE,input.getCommand());
		assertEquals(test,input.getDeleteID());
		
		String test7 = "done 1 2 3 4";
		input = parser.parse(test7);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.DONE,input.getCommand());
		assertEquals(test,input.getDoneID());
		
		String test8 =  "show";
		input = parser.parse(test8);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.SHOW,input.getCommand());
		
		String test9 = "";
		input = parser.parse(test9);
		assertFalse(input.getValid());
	}

}
