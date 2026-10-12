import java.io.*;
import java.util.*;

/**
 * CodeAlpha Task 1: Student Grade Tracker (console based)
 * Features: add/update/delete students, average/highest/lowest, summary report, file persistence.
 */
public class StudentGradeTracker {

    static class Student {
        private final int id;
        private String name;
        private double score;

        Student(int id, String name, double score) {
            this.id = id;
            this.name = name;
            this.score = score;
        }

        int getId() { return id; }
        String getName() { return name; }
        double getScore() { return score; }
        void setScore(double score) { this.score = score; }

        String getLetterGrade() {
            if (score >= 90) return "A";
            if (score >= 80) return "B";
            if (score >= 70) return "C";
            if (score >= 60) return "D";
            return "F";
        }
    }

    private static final String FILE_NAME = "grades.csv";
    private static final List<Student> students = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    private static int nextId = 1;

    public static void main(String[] args) {
        load();
        int choice;
        do {
            System.out.println("\n===== STUDENT GRADE TRACKER =====");
            System.out.println("1. Add student");
            System.out.println("2. Update student score");
            System.out.println("3. Delete student");
            System.out.println("4. View statistics (avg / highest / lowest)");
            System.out.println("5. Summary report");
            System.out.println("0. Save & Exit");
            choice = readInt("Choice: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> updateStudent();
                case 3 -> deleteStudent();
                case 4 -> showStats();
                case 5 -> summaryReport();
                case 0 -> { save(); System.out.println("Data saved. Goodbye!"); }
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    static void addStudent() {
        System.out.print("Student name: ");
        String name = sc.nextLine().trim().replace(",", " ");
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        double score = readScore();
        students.add(new Student(nextId++, name, score));
        System.out.println("Student added.");
    }

    static void updateStudent() {
        Student s = findById(readInt("Student ID: "));
        if (s == null) { System.out.println("Student not found."); return; }
        s.setScore(readScore());
        System.out.println("Score updated for " + s.getName());
    }

    static void deleteStudent() {
        Student s = findById(readInt("Student ID: "));
        if (s == null) { System.out.println("Student not found."); return; }
        students.remove(s);
        System.out.println("Deleted " + s.getName());
    }

    static void showStats() {
        if (students.isEmpty()) { System.out.println("No data yet."); return; }
        Student high = students.get(0), low = students.get(0);
        double total = 0;
        for (Student s : students) {
            total += s.getScore();
            if (s.getScore() > high.getScore()) high = s;
            if (s.getScore() < low.getScore()) low = s;
        }
        System.out.printf("Total students : %d%n", students.size());
        System.out.printf("Average score  : %.2f%n", total / students.size());
        System.out.printf("Highest score  : %.2f (%s)%n", high.getScore(), high.getName());
        System.out.printf("Lowest score   : %.2f (%s)%n", low.getScore(), low.getName());
    }

    static void summaryReport() {
        if (students.isEmpty()) { System.out.println("No data yet."); return; }
        System.out.println("\n---------------- SUMMARY REPORT ----------------");
        System.out.printf("%-5s %-20s %-8s %-6s%n", "ID", "Name", "Score", "Grade");
        System.out.println("------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-5d %-20s %-8.2f %-6s%n", s.getId(), s.getName(), s.getScore(), s.getLetterGrade());
        }
        System.out.println("------------------------------------------------");
        showStats();
    }

    static Student findById(int id) {
        for (Student s : students) if (s.getId() == id) return s;
        return null;
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    static double readScore() {
        while (true) {
            System.out.print("Score (0-100): ");
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= 0 && v <= 100) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a number between 0 and 100.");
        }
    }

    static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) pw.println(s.getId() + "," + s.getName() + "," + s.getScore());
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    static void load() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3) {
                    int id = Integer.parseInt(p[0]);
                    students.add(new Student(id, p[1], Double.parseDouble(p[2])));
                    nextId = Math.max(nextId, id + 1);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
        }
    }
}
