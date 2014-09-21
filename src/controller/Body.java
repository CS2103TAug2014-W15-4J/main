package controller;

import java.util.*;

/**
 * 
 * For temp use only
 * 
 * @author Lu Yuehan
 *
 */
public class Body {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean continues = true;
		String cmd;
		UserInput input = null;
		Parser parse = new Parser();
		do {
			cmd = scanner.nextLine();
			if (cmd.equals("exit"))
				continues = false;
			parse = new Parser();
			input = parse.parse(cmd);
			System.out.println(input.getCommand() + input.getEvent()
					+ input.getDeleteID() + input.getValid());
		} while (continues);
		scanner.close();
	}
}
