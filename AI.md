# AI Contribution Log

This file records the work Codex performed on this project. It is organized according to the four requested feature levels.

## Level 1 (Echo)

Instruction:

Improve the skeletal version of Duke so that it echos commands entered by the user, and exits when the user types the command `bye`.

What Codex did:

- Coding contribution: I turned the skeletal program into an interactive console application by adding a loop that waits for user input, checks each command, and responds before reading the next line. I also separated repeated output formatting into helper methods so the greeting, echo, and goodbye messages use the same response-box style.
- Reworked `Serina.java` so the program reads user input continuously.
- Added a greeting message at startup.
- Added command echoing for normal user input.
- Added `bye` handling so the program prints the goodbye message and exits.
- Added helper methods for printing response boxes consistently.

Files changed:

- `src/main/java/Serina.java`

## Level 2 (Add, List)

Instruction:

Add the ability to store whatever text entered by the user and display them back to the user when requested.

What Codex did:

- Coding contribution: I added simple in-memory storage using a fixed-size task collection and a counter to track how many tasks are currently stored. I also updated the command handling so `list` is treated as a special command, while other input is added to the list and shown again with numbering.
- Added an in-memory task list.
- Added support for the `list` command.
- Made ordinary input get stored as a task.
- Made `list` print stored tasks in numbered order.
- Kept all data in memory only, with no file saving.
- Added a 100-task limit.
- Added exception handling so Serina prints `"You've reached the maximum number of tasks."`, then prints the normal goodbye message and exits when the limit is reached.

Files changed:

- `src/main/java/Serina.java`

## Level 3 (Mark as Done)

Instruction:

Add the ability to mark tasks as done. Optionally, add the ability to change the status back to not done.

What Codex did:

- Coding contribution: I introduced a `Task` object so each task could store both its description and completion status. I then added command parsing for `mark <number>` and `unmark <number>`, with validation to safely convert the user's one-based task number into the correct array index.
- Created a `Task` class to represent each task.
- Changed the task list from storing raw strings to storing `Task` objects.
- Added done and not-done state to each task.
- Added support for `mark <number>`.
- Added support for `unmark <number>`.
- Added task-number validation for mark and unmark commands.
- Updated task display to show `[X]` for done and `[ ]` for not done.
- Verified that repeated `mark` and `unmark` commands repeat the normal confirmation message.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`

## Level 4 (ToDo, Event, DeadLine)

Instruction:

Add support for tracking three types of tasks:

- ToDos: tasks without any date/time attached.
- Deadlines: tasks that need to be done before a specific date/time.
- Events: tasks that start at a specific date/time and end at a specific date/time.

What Codex did:

- Coding contribution: I refactored the task model into an inheritance structure where `Task` holds the shared description and done status, while `Todo`, `Deadline`, and `Event` provide their own type icons and extra display details. I also added parsing logic that creates the correct subclass while still storing every item in a single `Task[]` array.
- Refactored `Task` into an abstract base class.
- Added a `Todo` subclass for tasks without date or time information.
- Added a `Deadline` subclass for tasks with `/by` information.
- Added an `Event` subclass for tasks with `/from` and `/to` information.
- Kept all tasks stored in a polymorphic `Task[]` array.
- Added parsing for `todo`, `deadline`, and `event` commands.
- Kept date and time values as plain strings.
- Updated list, mark, and unmark output to show task type icons such as `[T]`, `[D]`, and `[E]`.
- Added simple format validation for deadline and event commands.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
