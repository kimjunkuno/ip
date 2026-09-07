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
