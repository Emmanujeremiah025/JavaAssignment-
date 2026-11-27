# Programming in Java: Multithreaded Bank System & Student Records Manager

Two Java programs (plus a legacy applet) written for **Programming in Java (351 CS 2204)**, BSc Computer Science and Information Technology, DMI St. John the Baptist University, Mangochi.

The projects practise core Java topics: exception handling, multithreading and synchronization, applets, I/O streams, the `java.util` utility classes, and Swing event handling.

---

## Contents

| File | Description |
|------|-------------|
| `BankAccountSystem.java` | Console app: a depositor and a withdrawer share one bank account using threads |
| `BankApplet.java` | Legacy applet that displays parameters passed from HTML |
| `BankApplet.html` | HTML page that loads the applet and supplies its parameters |
| `StudentRecordsManager.java` | Swing GUI app to add, save, load, display and undo student records |

Suggested repository layout:

```
.
├── README.md
├── BankAccountSystem.java
├── BankApplet.java
├── BankApplet.html
└── StudentRecordsManager.java
```

---

## Requirements

- **JDK 8 or newer** for `BankAccountSystem` and `StudentRecordsManager`
- **JDK 8 specifically** (with `appletviewer`) for the applet. Modern browsers and newer JDKs no longer support applets.

Check your version:

```bash
java -version
javac -version
```

---

## Project 1: Multithreaded Bank Account System

A console program where two threads work on one shared `BankAccount` object.

### Features

- **User-defined checked exception**: `InsufficientFundsException` is thrown when a withdrawal exceeds MWK 500,000.
- **Synchronization**: `deposit()` and `withdraw()` are `synchronized`, so only one thread changes the balance at a time.
- **Thread coordination**: `wait()` makes the withdrawer pause when funds are too low; `notifyAll()` wakes it after a deposit. The balance is rechecked in a `while` loop.
- **Two ways to create threads**: `Depositor` extends `Thread`; `Withdrawer` implements `Runnable`.
- **Thread priorities** and `Thread.sleep()` to simulate real transaction timing.
- **`join()`** so the main thread waits for both workers before printing the final balance.
- **`try` / `catch` / `finally`** for input errors (`InputMismatchException`) and cleanup (closing the `Scanner`).

### Compile and run

```bash
javac BankAccountSystem.java
java BankAccountSystem
```

### Example session

```
Enter account holder's name: Grace
Enter opening balance (MWK): 100000

Account holder: Grace
Starting transactions...

Deposited MWK 50000.0. New balance: MWK 150000.0
Withdrew MWK 60000.0. New balance: MWK 90000.0
Deposited MWK 50000.0. New balance: MWK 140000.0
...
All transactions are complete.
Final balance for Grace: MWK 50000.0
Bank program is closing. Thank you.
```

> The order of messages can change between runs because the OS schedules the threads. With an opening balance of MWK 100,000, five deposits of MWK 50,000 and five withdrawals of MWK 60,000, the final balance is always MWK 50,000.

### Known limitation

The withdrawer waits until enough money is available. If the opening balance is too low for the total withdrawals to ever be covered (for example, `0`, since 5 × 50,000 deposited is less than 5 × 60,000 withdrawn), the withdrawer waits forever and the program does not exit. Use an opening balance of at least MWK 50,000 to see a normal run. A timed `wait(timeout)` or a "depositor finished" flag would be a good improvement.

---

## Project 1b: BankApplet (legacy)

A small applet that reads two parameters (`holder` and `bank`) with `getParameter()` and draws them in `paint()`. It also includes the applet life-cycle methods `init()`, `start()`, `stop()` and `destroy()`.

### Run (JDK 8 only)

```bash
javac BankApplet.java
appletviewer BankApplet.html
```

`BankApplet.html`:

```html
<html>
<body>
  <applet code="BankApplet.class" width="350" height="100">
    <param name="holder" value="Grace Banda">
    <param name="bank" value="National Bank of Malawi">
  </applet>
</body>
</html>
```

---

## Project 2: Student Records Manager

A Swing desktop app for managing student records, plus a console search that runs alongside it.

### Features

- Add a student with name, course and mark (validated to 0–100)
- **Display** all records in a text area
- **Save** to `students.txt` (character stream) and create a backup `students.bak` (byte stream)
- **Load** records back from `students.txt`
- **Undo** the most recently added record
- Console search by student name (case-insensitive) that reads `students.txt`

### Java concepts used

| Concept | Where it is used |
|---------|------------------|
| `Vector` + `Enumeration` | Storing and displaying records |
| `Stack` | Undo (`push`, `peek`, `pop`, `empty`) |
| `Random` | Generating unique six-digit student IDs |
| `Date` + `GregorianCalendar` | Timestamp for each record |
| `BufferedWriter` / `FileWriter` | Saving readable text (character stream) |
| `FileInputStream` / `FileOutputStream` | Creating the `.bak` backup (byte stream) |
| `BufferedReader` / `FileReader` | Loading records |
| `StringTokenizer` | Splitting each comma-separated line into fields |
| `Scanner` | Console search |
| `ActionListener` / `WindowAdapter` | Button clicks and window closing |
| try-with-resources | Automatic closing of file streams |

### Compile and run

```bash
javac StudentRecordsManager.java
java StudentRecordsManager
```

1. Enter a name in the console when prompted (the search reads `students.txt`, so save records from the window first).
2. Use the window to add, display, save, load and undo records.

### Example record

```
ID: 483921 | Name: Grace Banda | Course: Computer Science | Mark: 78.0 | Added: Thu Nov 26 18:00:00 CAT 2026
```

IDs and timestamps are generated at run time, so yours will differ.

### Known limitations

- Records are stored as comma-separated lines, so **commas are not allowed** in names or courses (the app checks for this when adding).
- The console prompt and the Swing window start at the same time, which is fine for a learning project but not ideal design.
- `Vector`, `Stack` and `Enumeration` are legacy classes, used here on purpose to practise them. Modern code would normally use `ArrayList`, `Deque` and `Iterator`.

---

## What I learned

- How synchronization and `wait()`/`notifyAll()` prevent race conditions on shared data
- When to use checked exceptions and how `throws` and `throw` work together
- The difference between byte streams and character streams
- How Swing handles events with listeners
- How the `java.util` collection and utility classes work

---

## Academic note

These programs were written for a university course. If you are a student on a similar course, use this repository to learn from, not to copy. Your institution's academic-integrity rules apply.

## Author

**Emmanuel**, Computer Science student

## License

Released for learning purposes. Add a license of your choice (for example MIT) if you want others to reuse the code.
