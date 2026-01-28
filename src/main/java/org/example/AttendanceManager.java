package org.example;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

public class AttendanceManager {
    static double attendanceLimit;

    public static void attendanceMenu(Student student, Scanner sc) {
        int choice = 0;

        while (choice != 4) {
            System.out.println("\nSelect an Option for Attendance Management:\n" +
                    "1 --> Set Attendance Limit\n" +
                    "2 --> Mark Attendance\n" +
                    "3 --> View Overall Attendance\n" +
                    "4 --> Return to Dashboard");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    setAttendanceLimit(sc);
                    break;
                case 2:
                    markAttendance(student, sc);
                    break;
                case 3:
                    viewOverallAttendance(student);
                    break;
                case 4:
                    System.out.println("Returning to dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public static void setAttendanceLimit(Scanner sc) {
        System.out.print("Enter new attendance limit percentage (e.g., 75 for 75%): ");
        double newLimit = sc.nextDouble();
        attendanceLimit = newLimit;
        System.out.println("Attendance limit set to " + newLimit + "%.");
    }

    public static void markAttendance(Student student, Scanner sc) {
        List<Course> courses = student.getCourses();

        if (courses.isEmpty()) {
            System.out.println("No registered courses available to mark attendance.");
            return;
        }

        String currentDate = java.time.LocalDate.now().toString(); // Get current date

        for (Course course : courses) {
            String courseID = course.getCourseID();
            System.out.println("\nMark attendance for " + course.getCourseName() + " on " + currentDate + ":");
            System.out.println("1. Present\n2. Absent\n3. No Class");

            int attendanceChoice;
            while (true) {
                System.out.print("Enter your choice: ");
                attendanceChoice = sc.nextInt();
                if (attendanceChoice >= 1 && attendanceChoice <= 3) break;
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }

            // Determine attendance status: 1 for Present, 0 for Absent, -1 for No Class
            int attendanceStatus;
            switch (attendanceChoice) {
                case 1 -> attendanceStatus = 1;  // Present
                case 2 -> attendanceStatus = 0;  // Absent
                default -> attendanceStatus = -1; // No Class
            }

            // Update attendance in the Student object
            student.getAttendance().putIfAbsent(courseID, new HashMap<>());
            student.getAttendance().get(courseID).put(currentDate, attendanceStatus);

            // Create an attendance entry document
            Document attendanceDoc = new Document("studentId", student.getUsername())
                    .append("courseId", courseID)
                    .append("date", currentDate)
                    .append("status", attendanceStatus);

            // Insert or update the attendance in the MongoDB collection
            MongoCollection<Document> attendanceCollection = MongoDBConnector.getAttendanceCollection();

            // Update if an entry exists for today; otherwise, insert a new one
            attendanceCollection.replaceOne(
                    Filters.and(Filters.eq("studentId", student.getUsername()),
                            Filters.eq("courseId", courseID),
                            Filters.eq("date", currentDate)),
                    attendanceDoc,
                    new ReplaceOptions().upsert(true) // Upsert if not exists
            );

            // Display the attendance details after marking
            System.out.println("Attendance for " + course.getCourseName() + " (" + courseID + ") on " + currentDate + ": " +
                    (attendanceStatus == 1 ? "Present" : attendanceStatus == 0 ? "Absent" : "No Class"));
        }

        // Update the student document in the database
        Student.updateStudentInDatabase(student);

        // Check for any course that has attendance below the limit
        checkAttendanceAlert(student);
    }

    private static void checkAttendanceAlert(Student student) {
        boolean alertNeeded = false;
        StringBuilder alertMessage = new StringBuilder("Alert: Attendance is below the set limit for the following courses:\n");

        for (String courseID : student.getAttendance().keySet()) {
            Map<String, Integer> dates = student.getAttendance().get(courseID);

            // Exclude "No Class" entries from totalClasses
            int totalClasses = (int) dates.values().stream().filter(status -> status != -1).count();
            int presentCount = (int) dates.values().stream().filter(status -> status == 1).count();
            double attendancePercentage = totalClasses > 0 ? (presentCount * 100.0 / totalClasses) : 0.0;

            if (attendancePercentage < attendanceLimit) {
                alertNeeded = true;
                String courseName = getCourseNameByID(student, courseID);
                alertMessage.append(courseName).append(" (").append(courseID).append("): ")
                        .append(String.format("%.2f%%", attendancePercentage)).append("\n");
            }
        }

        if (alertNeeded) {
            final String ANSI_RED = "\u001B[31m";
            final String ANSI_RESET = "\u001B[0m";
            System.out.println(ANSI_RED + alertMessage.toString() + ANSI_RESET);
        } else {
            System.out.println("Attendance marked successfully for all courses on " + java.time.LocalDate.now() + ".");
        }
    }

    public static void viewOverallAttendance(Student student) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-30s │ %-15s │ %-10s │ %-10s │ %-14s ║%n",
                "Course Name", "Total Classes", "Present", "Absent", "Attendance (%)");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════════════════════╣");

        Map<String, Map<String, Integer>> attendance = student.getAttendance();
        for (String courseID : attendance.keySet()) {
            String courseName = getCourseNameByID(student, courseID);
            Map<String, Integer> dates = attendance.get(courseID);

            // Exclude "No Class" entries from totalClasses
            int totalClasses = (int) dates.values().stream().filter(status -> status != -1).count();
            int presentCount = (int) dates.values().stream().filter(status -> status == 1).count();
            int absentCount = (int) dates.values().stream().filter(status -> status == 0).count();
            double attendancePercentage = totalClasses > 0 ? (presentCount * 100.0 / totalClasses) : 0.0;

            // Print each row with aligned columns and borders
            System.out.printf("║ %-30s │ %-15d │ %-10d │ %-10d │ %-14.2f%% ║%n",
                    courseName, totalClasses, presentCount, absentCount, attendancePercentage);
        }

        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════╝");
    }

    private static String getCourseNameByID(Student student, String courseID) {
        for (Course course : student.getCourses()) {
            if (course.getCourseID().equals(courseID)) {
                return course.getCourseName();
            }
        }
        return "Unknown Course";
    }
}
