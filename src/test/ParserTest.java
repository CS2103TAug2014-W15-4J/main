package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Logic;
import controller.Parser;
import controller.UserInput;

public class ParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void taskTypeTest() { // sample test
		Parser parser = new Parser();
		String test1 = "add go to sleep";
		UserInput input = parser.parse(test1);
		assertTrue(input.isFloat());
		assertFalse(input.isDeadline());
		assertFalse(input.isRepeated());
		
		String test2 = "add go to sleep by tomorrow";
		input = parser.parse(test2);
		assertTrue(input.isDeadline());
		assertFalse(input.isFloat());
		
		String test3 = "add go to sleep by 11 Oct";
		input = parser.parse(test3);
		assertTrue(input.isDeadline());
		assertFalse(input.isFloat());
	}

}
