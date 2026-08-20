# Serina UI Test Cases

This file records console-based test cases for Serina after Levels 1-5. Each test lists the user input sequence and the key output that should appear.

## How To Use

Compile and run Serina, then enter the inputs shown under each test case.

The exact divider lines are omitted below to keep the tests readable. The messages should still appear inside Serina's usual response boxes.

## Level 1 (Echo)

### Test 1.1 - Greet And Exit

Input:

```text
bye
```

Expected key output:

```text
Hello! I'm Serina
What can I do for you?
Bye. Hope to see you again soon!
```

Note: The early echo behavior was later replaced by task-specific command parsing. After Level 5, unknown commands are handled as errors instead of being echoed or added.

## Level 2 (Add, List)

### Test 2.1 - Add Todo Tasks And List Them

Input:

```text
todo read book
todo return book
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

### Test 2.2 - List When There Are No Tasks

Input:

```text
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

### Test 2.3 - Maximum Task Limit

Input:

```text
todo task 1
todo task 2
...
todo task 100
todo task 101
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] task 100
Now you have 100 tasks in the list.
You've reached the maximum number of tasks.
Bye. Hope to see you again soon!
```

## Level 3 (Mark as Done)

### Test 3.1 - Mark And Unmark A Task

Input:

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [T][X] return book
OK, I've marked this task as not done yet:
  [T][ ] return book
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

### Test 3.2 - Repeated Mark And Unmark

Input:

```text
todo read book
mark 1
mark 1
unmark 1
unmark 1
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [T][X] read book
Nice! I've marked this task as done:
  [T][X] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
Bye. Hope to see you again soon!
```

## Level 4 (ToDo, Event, DeadLine)

### Test 4.1 - Add All Three Task Types

Input:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

### Test 4.2 - Deadline With Flexible Date Text

Input:

```text
deadline do homework /by no idea :-p
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[D][ ] do homework (by: no idea :-p)
Bye. Hope to see you again soon!
```

### Test 4.3 - Mark Deadline And Event Tasks

Input:

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
mark 2
list
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
Nice! I've marked this task as done:
  [E][X] project meeting (from: Mon 2pm to: 4pm)
Here are the tasks in your list:
1.[D][X] return book (by: Sunday)
2.[E][X] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## Level 5 (Handle Errors)

### Test 5.1 - Empty Todo Description

Input:

```text
todo
bye
```

Expected key output:

```text
Sorry captain, todo descriptions can't be empty.
Bye. Hope to see you again soon!
```

### Test 5.2 - Unknown Command

Input:

```text
blah
bye
```

Expected key output:

```text
Sorry captain, could you rephrase that for me?
Bye. Hope to see you again soon!
```

### Test 5.3 - Invalid Deadline Commands

Input:

```text
deadline
deadline return book /by
deadline /by Sunday
bye
```

Expected key output:

```text
Sorry captain, please use: deadline <task> /by <time>
Sorry captain, deadlines need a /by time.
Sorry captain, deadline descriptions can't be empty.
Bye. Hope to see you again soon!
```

### Test 5.4 - Invalid Event Commands

Input:

```text
event
event meeting /from Mon
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon /to
bye
```

Expected key output:

```text
Sorry captain, please use: event <task> /from <start> /to <end>
Sorry captain, please use: event <task> /from <start> /to <end>
Sorry captain, event descriptions can't be empty.
Sorry captain, events need a /from time.
Sorry captain, events need a /to time.
Bye. Hope to see you again soon!
```

### Test 5.5 - Invalid Mark And Unmark Commands

Input:

```text
mark
mark abc
unmark 1
todo read book
mark 2
bye
```

Expected key output:

```text
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Sorry captain, please provide a valid task number.
Bye. Hope to see you again soon!
```
