package org.example;

import java.util.Scanner;

import static org.example.AttendanceManager.attendanceMenu;
import static org.example.Student.updateStudentInDatabase;

public class studentMenu {
    public static void main(Student student,Scanner sc) {
        int choice=0;
        System.out.println("WELCOME TO YOUR DASHBOARD");


        while(choice!=4){

            System.out.println("Select a Option\n" +
                    "1-->TO DO LIST\n"+
                    "2-->VIEW ASSIGNMENTS\n"+
                    "3-->ATTENDANCE MANAGEMENT\n"+
                    "4-->Logout");

            choice=sc.nextInt();

            switch (choice) {
                case 1:
                    toDoList(student,sc); // Call the registerTeacher method
                    break;
                case 2:
                    viewAssignments(student,sc); // Call the loginTeacher method
                    break;
                case 3:
                    attendanceMenu(student, sc);
                    break;
                case 4:
                    logoutStudent(student);
                    System.out.println("Logging out  Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }

        }

    }

    public static void toDoList(Student student, Scanner sc){
        int choice = 0;
        ToDoList todoList = student.getToDoList();

        // ANSI color codes for output
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        while (choice != 5) {
            System.out.println("\n" + ANSI_YELLOW + "----- TO-DO LIST MENU -----" + ANSI_RESET);
            System.out.println("1 --> View Non-Completed Tasks");
            System.out.println("2 --> View Completed Tasks");
            System.out.println("3 --> Add a New Task");
            System.out.println("4 --> Mark a Task as Completed");
            System.out.println("5 --> Return to Dashboard");

            choice = sc.nextInt();
            sc.nextLine();  // Consume newline left-over

            switch (choice) {
                case 1:
                    // View Non-Completed Tasks
                    System.out.println("\n" + ANSI_YELLOW + "Non-Completed Tasks:" + ANSI_RESET);
                    if (todoList.getNotCompleted().isEmpty()) {
                        System.out.println("No non-completed tasks available.");
                    } else {
                        int index = 1;
                        for (Task task : todoList.getNotCompleted()) {
                            System.out.println(index + ". " + task.getTaskDesc() + " (Due: " + task.getDueDate() + ")");
                            index++;
                        }
                    }
                    break;

                case 2:
                    // View Completed Tasks
                    System.out.println("\n" + ANSI_GREEN + "Completed Tasks:" + ANSI_RESET);
                    if (todoList.getCompleted().isEmpty()) {
                        System.out.println("No completed tasks available.");
                    } else {
                        int index = 1;
                        for (Task task : todoList.getCompleted()) {
                            System.out.println(index + ". " + task.getTaskDesc() + " (Completed on: " + task.getDueDate() + ")");
                            index++;
                        }
                    }
                    break;

                case 3:
                    // Add a New Task
                    System.out.print("Enter task description: ");
                    String description = sc.nextLine();
                    System.out.print("Enter due date (e.g., YYYY-MM-DD): ");
                    String dueDate = sc.nextLine();

                    // Determine the next task number
                    int taskNo = todoList.getNotCompleted().size() + todoList.getCompleted().size() + 1;
                    Task newTask = new Task(taskNo, description, dueDate);
                    todoList.addTask(newTask);

                    System.out.println("New task added successfully!");
                    student.setTodoList(todoList);  // Update the student's to-do list
                    updateStudentInDatabase(student);  // Save to the database
                    break;

                case 4:
                    // Mark a Task as Completed
                    System.out.println("Select a task number to mark as completed:");
                    if (todoList.getNotCompleted().isEmpty()) {
                        System.out.println("No non-completed tasks available.");
                    } else {
                        int taskIndex = 1;
                        for (Task task : todoList.getNotCompleted()) {
                            System.out.println(taskIndex + ". " + task.getTaskDesc() + " (Due: " + task.getDueDate() + ")");
                            taskIndex++;
                        }
                        int selectedTask = sc.nextInt();
                        sc.nextLine();  // Consume newline left-over

                        if (selectedTask > 0 && selectedTask <= todoList.getNotCompleted().size()) {
                            Task completedTask = todoList.getNotCompleted().get(selectedTask - 1);
                            todoList.completeTask(completedTask);

                            System.out.println("Task marked as completed successfully!");
                            student.setTodoList(todoList);  // Update the student's to-do list
                            updateStudentInDatabase(student);  // Save to the database
                        } else {
                            System.out.println("Invalid task number. Please try again.");
                        }
                    }
                    break;

                case 5:
                    System.out.println("Returning to dashboard...");
                    break;

                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }

    }

    public static void logoutStudent(Student student) {
        // Update the student in MongoDB before logging out
        updateStudentInDatabase(student);

        // Proceed with logout actions
        System.out.println("Logging out. Goodbye!");
        // Add any other necessary logout logic here
    }

    public static void viewAssignments(Student student,Scanner sc){
        // ANSI color codes
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        System.out.println(ANSI_RED + "----- VIEW ASSIGNMENTS -----" + ANSI_RESET);

        Assignments assignments = student.getAssignments();

        // Display non-completed assignments
        System.out.println(ANSI_RED + "Non-Completed Assignments:" + ANSI_RESET);
        int index = 1;
        for (Assignment assignment : assignments.getNotCompleted()) {
            System.out.println(index + ". Course ID: " + assignment.getCourseID());
            System.out.println("   Course Name: " + getCourseNameByID(student, assignment.getCourseID()));
            System.out.println("   Description: " + assignment.getAssignmentDesc());
            System.out.println("   Due Date: " + assignment.getDueDate());
            index++;
        }

        // Display completed assignments
        System.out.println("\n" + ANSI_GREEN + "Completed Assignments:" + ANSI_RESET);
        for (Assignment assignment : assignments.getCompleted()) {
            System.out.println("Course ID: " + assignment.getCourseID());
            System.out.println("Course Name: " + getCourseNameByID(student, assignment.getCourseID()));
            System.out.println("Description: " + assignment.getAssignmentDesc());
            System.out.println("Due Date: " + assignment.getDueDate());
        }

        // Ask the student if they want to mark an assignment as completed
        System.out.println("\nDo you want to mark any assignment as completed? (Enter assignment number or 0 to go back)");
        int choice = sc.nextInt();
        if (choice > 0 && choice <= assignments.getNotCompleted().size()) {
            Assignment assignmentToComplete = assignments.getNotCompleted().get(choice - 1);
            assignments.completeAssignment(assignmentToComplete);

            System.out.println("Assignment marked as completed successfully!");

            // Update the database with the modified assignment status
            student.setAssignments(assignments);
            updateStudentInDatabase(student); // Update the student's assignments in the database
        } else {
            System.out.println("Returning to dashboard...");
        }

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
