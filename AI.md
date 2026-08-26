# AI Contribution Log

This file records the work Codex performed on this project. It is organized according to the requested feature levels.

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

- Coding contribution: I refactored the task model into an inheritance structure where `Task` holds the shared description and done status, while `Todo`, `Deadline`, and `Event` provide their own type icons and extra display details. I also added parsing logic that creates the correct subclass while storing every item through the shared `Task` type.
- Refactored `Task` into an abstract base class.
- Added a `Todo` subclass for tasks without date or time information.
- Added a `Deadline` subclass for tasks with `/by` information.
- Added an `Event` subclass for tasks with `/from` and `/to` information.
- Kept all tasks stored through the shared `Task` type so `Todo`, `Deadline`, and `Event` objects could be handled polymorphically.
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

## Level 5 (Handle Errors)

Instruction:

Teach the chatbot to deal with errors such as incorrect inputs entered by the user. Use exceptions to handle errors, including a custom exception class named to match the chatbot.

What Codex did:

- Coding contribution: I added a custom `SerinaException` class and changed the command parser so invalid user input is reported through exceptions instead of being silently accepted. I also made `Serina.java` catch these exceptions in one place and print friendly messages without crashing the chatbot.
- Added `SerinaException` for chatbot-specific errors.
- Made unknown commands such as `blah` produce `Sorry captain, could you rephrase that for me?`.
- Made empty todo commands such as `todo` produce `Sorry captain, todo descriptions can't be empty.`.
- Added clearer error messages for invalid `deadline`, `event`, `mark`, and `unmark` inputs.
- Stopped treating unrecognized input as a default todo task.
- Kept the 100-task-limit exception behavior, with Serina still printing the goodbye message before exiting.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/SerinaException.java`
- `AI.md`

## Level 6 (Delete)

Instruction:

Add support for deleting tasks from the list. Use Java Collections classes, such as `ArrayList<Task>`, for storing data.

What Codex did:

- Coding contribution: I refactored Serina's task storage from a fixed `Task[]` array plus manual counter into an `ArrayList<Task>`. This made deletion simpler because Serina can remove the selected task directly and the remaining tasks shift automatically.
- Added support for `delete <number>`.
- Added a helper method that removes and returns the selected task.
- Reused the same task-number validation logic used by `mark` and `unmark`.
- Added a delete confirmation message that shows the removed task and the updated task count.
- Kept the earlier 100-task limit behavior while using `ArrayList<Task>` for storage.

Files changed:

- `src/main/java/Serina.java`
- `AI.md`

## Level A-enums

Instruction:

Use Java enums where they are a natural fit. Amend exception handling to reflect the enum change and update the UI test cases.

What Codex did:

- Coding contribution: I introduced enums for fixed sets of values that should not be represented by loose strings or booleans. Task type icons now come from `TaskType`, task completion state now comes from `TaskStatus`, and Serina-specific error messages now come from `SerinaError`.
- Added `TaskType` for `TODO`, `DEADLINE`, and `EVENT` task icons.
- Added `TaskStatus` for `DONE` and `NOT_DONE` status icons.
- Added `SerinaError` to centralize error messages and whether an error should exit the chatbot.
- Updated `Task` so it uses `TaskStatus` instead of a boolean done flag.
- Updated `Todo`, `Deadline`, and `Event` so they return a `TaskType` instead of hardcoded type icon strings.
- Updated `SerinaException` so it stores a `SerinaError` enum value.
- Simplified exception handling in `Serina.java` so the catch block checks `e.shouldExit()` instead of catching a separate task-limit exception subclass.
- Updated `test-ui.md` with enum regression tests.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/SerinaException.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Level 7 (Save)

Instruction:

Save the tasks on the hard disk automatically whenever the task list changes. For now, implement only the minimal happy path for writing to the file; loading from the file will be implemented later.

What Codex did:

- Coding contribution: I added a small storage layer that writes the current task list to `data/serina.txt` after commands that change the list. This covers adding, marking, unmarking, and deleting tasks while leaving startup loading for a later level.
- Added `Storage` to handle creating the `data` folder and writing task lines to disk.
- Added `toFileString()` behavior to `Task` so all task types share the same save format.
- Added deadline and event save fields so their `/by`, `/from`, and `/to` values are preserved in the file.
- Extended `TaskStatus` so done status saves as `1` and not-done status saves as `0`.
- Added a Serina-specific save error in `SerinaError` for unexpected write failures.
- Updated `test-ui.md` with save-file test cases.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Task.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/SerinaError.java`
- `src/main/java/Storage.java`
- `AI.md`
- `test-ui.md`

## Storage (Load)

Instruction:

Load saved task data from the hard disk when the chatbot starts up, using the same file written in the previous save level.

What Codex did:

- Coding contribution: I completed the storage loop by teaching Serina to read `data/serina.txt` on startup and rebuild the in-memory task list before the user enters commands.
- Added `Storage.loadTasks()` so a missing save file starts Serina with an empty list.
- Added parsing for saved todo, deadline, and event lines.
- Added enum helpers in `TaskType` and `TaskStatus` so saved type and status values are converted back safely.
- Added constructors that allow saved tasks to be recreated with their existing done/not-done status.
- Added a Serina-specific load error in `SerinaError`; if loading fails, Serina reports the issue and continues with an empty list.
- Updated `test-ui.md` with load-file test cases.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Storage.java`
- `src/main/java/Task.java`
- `src/main/java/Todo.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Storage (Edge Cases)

Instruction:

Enhance the code to handle edge cases and possible errors, while continuing to use relative file paths instead of absolute paths.

What Codex did:

- Coding contribution: I tightened the persistence code so bad or unusual save-file data cannot crash Serina. The storage path remains relative as `data/serina.txt`, which keeps the app portable across computers.
- Made Serina greet first, then report any load problem before accepting commands.
- Confirmed Serina starts normally when `data/serina.txt` and the `data` folder do not exist yet.
- Made the loader ignore blank lines in the save file.
- Added validation for malformed saved tasks, unknown saved task types, invalid saved status values, missing descriptions, and missing deadline/event fields.
- Added a clear error for save files containing more than 100 tasks.
- Escaped `|` and `\` when saving task fields so user-entered text containing the file delimiter can still be saved and loaded correctly.
- Updated `test-ui.md` with edge-case persistence tests, including first-run startup without an existing save file.

Files changed:

- `src/main/java/Serina.java`
- `src/main/java/Storage.java`
- `src/main/java/Task.java`
- `src/main/java/Deadline.java`
- `src/main/java/Event.java`
- `src/main/java/TaskType.java`
- `src/main/java/TaskStatus.java`
- `src/main/java/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Level 8 (Dates and Times)

Instruction:

Teach Serina how to understand dates and times instead of treating deadline and event date fields as plain strings.

What Codex did:

- Coding contribution: I changed deadline and event date fields from raw `String` values into `LocalDate` values, so Serina now parses and stores real dates.
- Added `DateParser` to centralize date parsing and display formatting.
- Updated deadline and event commands to accept dates in `yyyy-MM-dd` format.
- Updated task display so saved dates are shown in a friendlier format such as `Dec 2 2019`.
- Updated save/load behavior so dates are stored in the stable `yyyy-MM-dd` format on disk.
- Added validation for invalid date formats and event end dates that come before start dates.
- Added a `find <date>` command that lists deadlines and events occurring on a given date.
- Updated `test-ui.md` with date parsing, date formatting, invalid date, and `find` command tests.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/Task.java`
- `src/main/java/serina/Deadline.java`
- `src/main/java/serina/Event.java`
- `src/main/java/serina/DateParser.java`
- `src/main/java/serina/Storage.java`
- `src/main/java/serina/SerinaError.java`
- `AI.md`
- `test-ui.md`

## Help Command

Instruction:

Add a `help` command that lists every command Serina understands, and mention the command in the greeting and
unknown-command error message.

What Codex did:

- Added `help` handling to Serina's command loop without changing the task list or save file.
- Added a concise description and usage format for every supported command.
- Updated the greeting so new users know that they can type `help`.
- Updated the unknown-command error so users are directed to the command list.
- Updated `test-ui.md` with the new greeting and error wording and a complete help-command test.

Files changed:

- `src/main/java/serina/Serina.java`
- `src/main/java/serina/SerinaError.java`
- `AI.md`
- `test-ui.md`
