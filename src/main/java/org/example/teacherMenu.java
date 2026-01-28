package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.Student.updateStudentInDatabase;
import static org.example.Attendance.attendanceMenu;

public class teacherMenu {
    public static void Main(Teacher teacher,Scanner sc) {
        int choice;
        System.out.println("WELCOME TO YOUR TEACHER DASHBOARD");

        do {
            System.out.println("\nSelect an Option\n" +
                    "1-->POST ASSIGNMENT\n" +
                    "2-->ATTENDANCE\n" +
                    "3-->LOGOUT");

            choice=sc.nextInt();

            switch (choice) {
                case 1:
                    postAssignment(teacher, sc);
                    break;
                case 2:
                    attendanceMenu(teacher, sc);
                    break;
                case 3:
                    logoutTeacher(teacher);
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }

        } while (choice != 3);
    }


    public static void postAssignment(Teacher teacher,Scanner sc) {
        sc.nextLine();
        MongoCollection<Document> studentCollection = MongoDBConnector.getMongoDatabase().getCollection("Student");

        // Show the courses the teacher teaches
        System.out.println("Courses you teach:");
        for (Course course : teacher.getCourses()) {
            System.out.println(" - Course ID: " + course.getCourseID() + ", Course Name: " + course.getCourseName());
        }

        // Ask for courseID, description, and due date
        System.out.print("Enter the Course ID for the assignment: ");
        String courseID = sc.nextLine();

        // Check if the teacher teaches this course
        boolean isCourseValid = teacher.getCourses().stream().anyMatch(course -> course.getCourseID().equals(courseID));
        if (!isCourseValid) {
            System.out.println("Invalid Course ID. Please select a valid course ID.");
            return;
        }

        System.out.print("Enter the assignment description: ");
        String assignmentDesc = sc.nextLine();

        System.out.print("Enter the due date (format: YYYY-MM-DD): ");
        String dueDate = sc.nextLine();

        // Create the assignment
        Assignment assignment = new Assignment(courseID, assignmentDesc, dueDate);

        // Find all students enrolled in this course and add the assignment to their notCompleted list
        List<Document> studentsEnrolled = studentCollection.find(Filters.eq("courses.courseID", courseID)).into(new ArrayList<>());

        for (Document studentDoc : studentsEnrolled) {
            // Add assignment to the student's notCompleted list
            Document assignmentDoc = new Document("courseID", assignment.getCourseID())
                    .append("assignmentDesc", assignment.getAssignmentDesc())
                    .append("dueDate", assignment.getDueDate());

            // Update the student's notCompleted assignments list
            studentCollection.updateOne(
                    Filters.eq("username", studentDoc.getString("username")),
                    Updates.push("assignments.notCompleted", assignmentDoc)
            );
        }

        System.out.println("Assignment posted successfully for course ID: " + courseID);
    }

    public static void logoutTeacher(Teacher teacher) {
        MongoCollection<Document> teacherCollection = MongoDBConnector.getMongoDatabase().getCollection("Teacher");

        // Convert the Teacher object to a MongoDB document
        Document updatedTeacherDoc = teacher.toDocument();

        // Update the MongoDB entry with the new document using the teacher's username as the filter
        teacherCollection.replaceOne(Filters.eq("username", teacher.getUsername()), updatedTeacherDoc);

        System.out.println("Teacher data successfully updated in MongoDB.");
    }


}
