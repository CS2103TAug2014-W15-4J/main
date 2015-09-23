uClear
====

2014 CS2103T Project of team W15-4j

User Guide
====
###Adding tasks
1.Add a floating task:

```
add <event> 
```
e.g. add going to movie

2.Add a fixed task:

```
add <event> from <time> to <time>
```

e.g. add going to school from 14 to 18 on 9 Sep 2014

3.Add a repeated task:

```
add <event> every <time> [daily/weekly/monthly]
```
e.g. add CS1231 every 14 to 16 Friday 

4.Add a deadline(of a task):

```
add <event> by <time>
```
e.g. add CS2102 project by 25 Sep 2014

###Editing tasks
If nothing is specified after the eventID, no changes will be made (Error message)
Other than edit, update can also be used.

1.When viewing tasks, to edit task details:

```
edit <taskID><event>
```
e.g. edit 2 return books at library

2.When viewing tasks, to edit the <start/end> time of the task:
```
edit <taskID><time>
```
e.g. edit 2 from 9 to 11 (for timed tasks)

3.When viewing tasks, to set a task to be repeated:
```
edit <taskID> every <time> [daily/weekly/monthly]
```
e.g. edit 2 every Saturday

***It is also possible to edit some (or all) fields at the same time:***
```
edit <taskID> <event> <time> 
```
e.g. edit 2 return books at library from 9 to 11 every Saturday

###Delete tasks
1.Delete a task with a specific ID
```
delete <taskID>  
```
e.g. delete 1

2.Delete several tasks with id
```
delete <taskID1> <taskID2>…
```
e.g. delete 1 3 4

3.Clear all the tasks (Confirmation required; cannot undo)
```
clear
```

###Searching tasks
Search task(s) by a keyword: 
```
search <keyword> 
```
e.g. search exercise

###Tagging
Tag a specific task

```
tag <taskID> <tag> 
```
e.g. tag 3 CS2103T

###Done a task

1.Finish a task with a specific ID
```
done <taskID>
```

e.g. done 1 (mark task 1 as done)

2.Finish several tasks with ID1, ID2, ID3…
```
done <taskID1> <taskID2>...
```

e.g. done 1 2 3 (mark task 1, 2, 3 as done, separated by a space)

###Undo an operation
Undo reverts the last operation that caused a change (e.g. add, delete, edit)
```
undo
```

###Redo an operation
Redo the last operation that caused a change (e.g. add, delete, edit)
```
redo
```
	
###Display Task

1.Display all the tasks to be done on the screen
```
show / show all
```

2.Display tasks which has been done
```
show done
```

3.Show all the tasks which have due days
```
show overdue
```

4.Display all tasks due today
```
show today
```

5.Display all the tasks due tomorrow
```
show tmr
show tomorrow
```

6.Display all the task due on a specific date
```
show <time>
```

7.Display all the tasks due within a time range
```
show from <date> to <date>
```

8.Display all tasks with the tag: 
```
show <tag> 
```

***Time format***

*\<time\>* can only be in the following formats: 

1.	\<time\> on \<date\> 
	e.g. 12 on 9 September(or Sep) 2014
2.	\<time\> \<day\> 
	e.g. 12 today/tomorrow(or tmr)/Monday(or Mon)
3.	\<time\> to \<time\> 
	e.g. 14 to 18 
4.	\<time\> \<date\> to \<time\> \<date\> 
	e.g. 14 today / tomorrow(or tmr) / Monday(or Mon)
           to 18 today / tomorrow(or tmr) / Monday(or Mon)
5.	\<time\> on \<date\> to \<time\> on \<date\> 
	e.g. 14 on 9 Sep to 18 on 10 Sep 2014


