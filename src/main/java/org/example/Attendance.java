package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

public class Attendance {
    public static void attendanceMenu(Teacher teacher, Scanner sc) {
        int choice = 0;

        while (choice != 4) {
            System.out.println("\nSelect an Option for Attendance Management:\n" +
                    "1 --> Mark Attendance\n" +
                    "2 --> View Student Attendance\n" +
                    "3 --> Return to Dashboard");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    markAttendanceForTeacher(teacher, sc);
                    break;
                case 2:
                    viewStudentAttendance(teacher);
                    break;
                case 3:
                    System.out.println("Returning to dashboard...");
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public static void markAttendanceForTeacher(Teacher teacher, Scanner sc) {
        // Retrieve teacher's courses
        List<Course> courses = teacher.getCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses assigned to you.");
            return;
        }

        String currentDate = java.time.LocalDate.now().toString(); // Get the current date

        for (Course course : courses) {
            String courseID = course.getCourseID();
            System.out.println("\nMark your attendance for " + course.getCourseName() + " on " + currentDate + ":");

            // Prompt the teacher to mark their attendance for this specific course
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

            // Update attendance for the teacher in the database
            MongoCollection<Document> teacherAttendanceCollection = MongoDBConnector.getTeacherAttendanceCollection();

            Document attendanceDoc = new Document("teacherId", teacher.getUsername())
                    .append("courseId", courseID)
                    .append("date", currentDate)
                    .append("status", attendanceStatus);

            teacherAttendanceCollection.updateOne(
                    Filters.and(
                            Filters.eq("teacherId", teacher.getUsername()),
                            Filters.eq("courseId", courseID),
                            Filters.eq("date", currentDate)
                    ),
                    new Document("$set", attendanceDoc),
                    new com.mongodb.client.model.UpdateOptions().upsert(true) // Create new if it doesn't exist
            );

            // Display attendance status to the user
            System.out.println("Attendance marked for course " + course.getCourseName() + ": " +
                    (attendanceStatus == 1 ? "Present" : attendanceStatus == 0 ? "Absent" : "No Class"));
        }
    }

    public static void viewStudentAttendance(Teacher teacher) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-30s │ %-15s │ %-20s  ║%n",
                "Course Name", "Student ID", "Attendance (%)");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");

        List<Course> courses = teacher.getCourses();

        for (Course course : courses) {
            String courseID = course.getCourseID();
            String courseName = course.getCourseName();

            List<Student> students = Student.getStudentsByCourse(courseID);
            for (Student student : students) {
                Map<String, Integer> dates = student.getAttendance().get(courseID);
                int totalClasses = dates.size();
                int presentCount = (int) dates.values().stream().filter(status -> status == 1).count();
                double attendancePercentage = totalClasses > 0 ? (presentCount * 100.0 / totalClasses) : 0.0;

                System.out.printf("║ %-30s │ %-15s │ %-20.2f%% ║%n",
                        courseName, student.getUsername(), attendancePercentage);
            }
        }

        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
    }
}
