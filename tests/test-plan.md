# Serina Test Plan

## Flexible Multi-Term Search

Run each test with a clean save file unless the test supplies its own setup commands.

### Find Partial Keywords In Any Order

Input:

```text
todo Submit Project report
todo Draft project slides
todo Repair printer
find REP PROJ
```

Expected search output:

```text
Here are the matching tasks in your list:
1.[T][ ] Submit Project report
```

### Require Every Keyword In One Description

Input:

```text
todo read book
todo buy bread
find book bread
```

Expected search output:

```text
Here are the matching tasks in your list:
```

### Allow One-Character Keywords

Input:

```text
todo read book
todo buy lunch
find b
```

Expected search output:

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[T][ ] buy lunch
```

### Reject A Missing Query

Input:

```text
find
```

Expected output:

```text
Sorry captain, find needs a keyword.
```

### Preserve Stored Tasks

After running any valid `find` command, restart Serina and run `list`.

Expected behavior: all tasks, statuses, dates, and ordering are unchanged.

## More Error Handling

### Normalize harmless whitespace

Enter `  todo   inspect engines  `, followed by `list`. The task is accepted and displayed without the outer
whitespace.

### Reject malformed date parameters

Verify that a deadline containing two `/by` parameters and an event containing `/to` before `/from` show errors
without changing the task list. Verify that `2026-02-30` and an event with equal start and end dates are rejected.

### Reject normalized duplicates

Add `todo Inspect engines`, then enter `todo   inspect   ENGINES`. Serina should identify the existing task number
and retain one task.

### Recover after reaching capacity

Fill the roster with 100 unique tasks. The next addition should be rejected without exiting. Delete one task and
verify that another unique task can then be added.

### Preserve data after a save failure

Make the save location unwritable, then try adding, deleting, marking, and unmarking a task. Each command should
report that it was not applied. Restore access and verify that both the displayed list and save file contain the
original data.

### Protect an invalid save file

Place an invalid record on line 2 of the save file and start Serina. Serina should identify line 2, keep `help` and
`bye` usable, and reject task-data commands. Verify that the invalid file remains unchanged.
