# Duke User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Finding tasks

Use `find <keywords>` to find tasks whose descriptions contain every keyword. Matching ignores letter case, each
keyword may match part of a word, and keywords may appear in any order.

Example: `find proj rep`

Given a task named `Submit Project report`, the expected output is:

```text
Matching tasks, Captain Cutter:
1.[T][ ] Submit Project report
```

Searches examine task descriptions only. Results remain in task-list order and are numbered from `1` within the
matching results. When nothing matches, Serina reports that no matching tasks were found and suggests trying
different keywords. A `find` command without keywords is rejected and shows the required command format.

## Correcting invalid commands

Serina accepts leading, trailing, and repeated whitespace between command components. Commands that omit a
required value, repeat a date parameter, put `/to` before `/from`, or add arguments to `help`, `list`, or `bye`
are rejected with the required format. Dates must be real calendar dates written as `yyyy-MM-dd`.

An event's end date must be later than its start date. Serina also rejects duplicate tasks of the same type with
the same description and dates; capitalization and repeated spaces do not make a task unique. In the GUI, an
invalid command remains in the input field so it can be corrected.

## Recovering from storage errors

Serina writes changes to a temporary file before atomically replacing `data/serina.txt`. If saving fails, the
command is not applied and the previous task list remains available. Check that the `data` folder is writable,
then retry the command.

If the save file cannot be read or contains an invalid record, Serina reports the affected line and prevents
task-data commands from overwriting it. Repair the file or its permissions and restart Serina. The `help` and
`bye` commands remain available during recovery.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
