package test;
import com.joestelmach.natty.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

public class NattyTest {
	public static void main(String[] args) {

		List<Date> dateList = new ArrayList<Date>();
		Parser parser = new Parser();
		System.out.println("Enter something with a date/time:");
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		List<DateGroup> groups = parser.parse(input);
		for(DateGroup group:groups) {
		  List<Date> dates = group.getDates();
		  int line = group.getLine();
		  int column = group.getPosition();
		  String matchingValue = group.getText();
		  String syntaxTree = group.getSyntaxTree().toStringTree();
		  Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
		  boolean isRecurreing = group.isRecurring();
		  Date recursUntil = group.getRecursUntil();
		  dateList.addAll(dates);
		}
		System.out.println(dateList);
	}
}
