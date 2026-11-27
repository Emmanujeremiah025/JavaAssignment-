import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// Student record class
class StudentRecord {

    int id;
    String name;
    String course;
    double mark;
    long timeAdded;

    StudentRecord(int id, String name,
                  String course, double mark,
                  long timeAdded) {

        this.id = id;
        this.name = name;
        this.course = course;
        this.mark = mark;
        this.timeAdded = timeAdded;
    }

    public String toString() {

        return "ID: " + id
            + " Name: " + name
            + " Course: " + course
            + " Mark: " + mark
            + " Date: " + new Date(timeAdded);
    }

    public String toFileLine() {

        return id + "," + name + ","
            + course + "," + mark + ","
            + timeAdded;
    }
}

// Main class
public class StudentRecordsManager extends JFrame {

    JTextField nameField = new JTextField(15);
    JTextField courseField = new JTextField(15);
    JTextField markField = new JTextField(10);

    JTextArea output = new JTextArea(15, 40);

    Vector<StudentRecord> records = new Vector<>();

    Stack<StudentRecord> undoStack = new Stack<>();

    Random random = new Random();

    public StudentRecordsManager() {

        setTitle("Student Records Manager");

        setSize(600, 450);

        setLayout(new BorderLayout());

        // Input panel
        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Student Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Course:"));
        panel.add(courseField);

        panel.add(new JLabel("Mark:"));
        panel.add(markField);

        add(panel, BorderLayout.NORTH);

        // Buttons
        JPanel buttons = new JPanel();

        JButton add = new JButton("Add");
        JButton save = new JButton("Save");
        JButton load = new JButton("Load");
        JButton display = new JButton("Display");
        JButton undo = new JButton("Undo");

        buttons.add(add);
        buttons.add(save);
        buttons.add(load);
        buttons.add(display);
        buttons.add(undo);

        add(buttons, BorderLayout.SOUTH);

        output.setEditable(false);

        add(new JScrollPane(output), BorderLayout.CENTER);

        // Event handling
        add.addActionListener(e -> addRecord());

        save.addActionListener(e -> saveRecords());

        load.addActionListener(e -> loadRecords());

        display.addActionListener(e -> displayRecords());

        undo.addActionListener(e -> undoRecord());

        // Window closing event
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {

                dispose();
                System.exit(0);
            }
        });

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    // Generate unique ID
    int generateID() {

        int id;
        boolean exists;

        do {

            id = 100000 + random.nextInt(900000);

            exists = false;

            for (StudentRecord r : records) {

                if (r.id == id) {
                    exists = true;
                    break;
                }
            }

        } while (exists);

        return id;
    }

    // Add student
    void addRecord() {

        String name = nameField.getText().trim();

        String course = courseField.getText().trim();

        if (name.isEmpty() || course.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Enter name and course.");

            return;
        }

        if (name.contains(",") || course.contains(",")) {

            JOptionPane.showMessageDialog(
                this, "Commas are not allowed.");

            return;
        }

        try {

            double mark =
                Double.parseDouble(markField.getText());

            if (mark < 0 || mark > 100) {

                JOptionPane.showMessageDialog(
                    this, "Mark must be between 0 and 100.");

                return;
            }

            GregorianCalendar calendar =
                new GregorianCalendar();

            Date date = calendar.getTime();

            StudentRecord student =
                new StudentRecord(
                    generateID(),
                    name,
                    course,
                    mark,
                    date.getTime()
                );

            records.add(student);

            undoStack.push(student);

            JOptionPane.showMessageDialog(
                this, "Student added successfully.");

            nameField.setText("");
            courseField.setText("");
            markField.setText("");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Invalid mark. Enter a number.");
        }
    }

    // Display records using Enumeration
    void displayRecords() {

        output.setText("");

        Enumeration<StudentRecord> list =
            records.elements();

        if (!list.hasMoreElements()) {

            output.setText("No records found.");

            return;
        }

        while (list.hasMoreElements()) {

            output.append(
                list.nextElement().toString() + "\n");
        }
    }

    // Undo latest record using Stack
    void undoRecord() {

        if (undoStack.empty()) {

            JOptionPane.showMessageDialog(
                this, "Nothing to undo.");

            return;
        }

        StudentRecord last = undoStack.peek();

        undoStack.pop();

        records.remove(last);

        output.setText("Last record removed:\n"
                + last.toString());
    }

    // Save records
    void saveRecords() {

        try {

            BufferedWriter writer =
                new BufferedWriter(
                    new FileWriter("students.txt"));

            for (StudentRecord r : records) {

                writer.write(r.toFileLine());

                writer.newLine();
            }

            writer.close();

            // Create backup using byte stream
            FileInputStream input =
                new FileInputStream("students.txt");

            FileOutputStream backup =
                new FileOutputStream("students.bak");

            byte[] buffer = new byte[1024];

            int count;

            while ((count = input.read(buffer)) != -1) {

                backup.write(buffer, 0, count);
            }

            input.close();
            backup.close();

            JOptionPane.showMessageDialog(
                this, "Records saved successfully.");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this, "File error: " + e.getMessage());
        }
    }

    // Load records
    void loadRecords() {

        Vector<StudentRecord> loaded = new Vector<>();

        try {

            BufferedReader reader =
                new BufferedReader(
                    new FileReader("students.txt"));

            String line;

            while ((line = reader.readLine()) != null) {

                StringTokenizer tokens =
                    new StringTokenizer(line, ",");

                if (tokens.countTokens() != 5) {
                    continue;
                }

                int id =
                    Integer.parseInt(tokens.nextToken());

                String name = tokens.nextToken();

                String course = tokens.nextToken();

                double mark =
                    Double.parseDouble(tokens.nextToken());

                long time =
                    Long.parseLong(tokens.nextToken());

                loaded.add(
                    new StudentRecord(
                        id, name, course, mark, time));
            }

            reader.close();

            records.clear();

            records.addAll(loaded);

            undoStack.clear();

            for (StudentRecord r : records) {
                undoStack.push(r);
            }

            displayRecords();

            JOptionPane.showMessageDialog(
                this, "Records loaded successfully.");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this, "File error: " + e.getMessage());

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Invalid data in file.");
        }
    }

    // Search student using Scanner
    static void searchStudent() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter student name: ");

        String searchName = scanner.nextLine();

        boolean found = false;

        try {

            BufferedReader reader =
                new BufferedReader(
                    new FileReader("students.txt"));

            String line;

            while ((line = reader.readLine()) != null) {

                StringTokenizer tokens =
                    new StringTokenizer(line, ",");

                if (tokens.countTokens() == 5) {

                    String id = tokens.nextToken();

                    String name = tokens.nextToken();

                    String course = tokens.nextToken();

                    String mark = tokens.nextToken();

                    String time = tokens.nextToken();

                    if (name.equalsIgnoreCase(searchName)) {

                        System.out.println("Student Found!");

                        System.out.println(
                            "ID: " + id
                            + " Name: " + name
                            + " Course: " + course
                            + " Mark: " + mark
                            + " Time: " + time);

                        found = true;
                    }
                }
            }

            reader.close();

            if (!found) {

                System.out.println("Student not found.");
            }

        } catch (IOException e) {

            System.out.println("File error: " + e.getMessage());

        } finally {

            scanner.close();
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
            () -> new StudentRecordsManager());

        // Console search can be run separately
        // after saving records to students.txt.
    }
}