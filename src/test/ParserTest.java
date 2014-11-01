package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import controller.Parser;
import controller.UserInput;
import controller.UserInput.RepeatDate;

public class ParserTest {

	@Test
	public void taskTypeTest() {
		// test add floating
		Parser parser = new Parser();
		String test1 = "add go to sleep";
		UserInput input = parser.parse(test1);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD, input.getCommand());
		assertTrue(input.isFloat());
		assertFalse(input.isDeadline());
		// test add deadline
		String test2 = "add go to sleep tomorrow";
		input = parser.parse(test2);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD, input.getCommand());
		assertTrue(input.isDeadline());
		assertFalse(input.isFloat());
		assertEquals(input.getDate().size(), 1);
		// test add repeated
		String test3 = "add go to sleep every 2pm daily";
		input = parser.parse(test3);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.ADD, input.getCommand());
		assertFalse(input.isDeadline());
		assertFalse(input.isFloat());
		assertTrue(input.isRepeated());
		assertEquals(input.repeatDate(), RepeatDate.DAILY);
		// test wrong form of edit
		String test4 = "edit 2";
		input = parser.parse(test4);
		assertFalse(input.getValid());
		// test edit
		String test5 = "edit 2 going to school 11pm to 6pm";
		input = parser.parse(test5);
		assertTrue(input.getValid());
		assertEquals(2, input.getEditID());
		assertEquals(UserInput.CMD.EDIT, input.getCommand());
		assertEquals("going to school", input.getDescription());
		// test delete
		String test6 = "delete 1 2 3 4";
		input = parser.parse(test6);
		List<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.DELETE, input.getCommand());
		assertEquals(test, input.getDeleteID());
		// test done
		String test7 = "done 1 2 3 4";
		input = parser.parse(test7);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.DONE, input.getCommand());
		assertEquals(test, input.getDoneID());
		// test show
		String test8 = "show";
		input = parser.parse(test8);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.SHOW, input.getCommand());
		// test tag
		String test9 = "tag 1 tags";
		input = parser.parse(test9);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.TAG, input.getCommand());
		// test untag
		String test11 = "untag 1 tags";
		input = parser.parse(test11);
		assertTrue(input.getValid());
		assertEquals(UserInput.CMD.UNTAG, input.getCommand());
		// test wrong input
		String test10 = "";
		input = parser.parse(test10);
		assertFalse(input.getValid());
	}
}
