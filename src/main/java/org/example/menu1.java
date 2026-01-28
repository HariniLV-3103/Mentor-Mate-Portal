package org.example;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import com.mongodb.client.model.Filters;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class menu1 {

    // DataManager instances for managing collections of Student, Teacher, and Course
    private static final DataManager<Student> studentManager = new DataManager<>();
    private static final DataManager<Teacher> teacherManager = new DataManager<>();
    private static final DataManager<Course> courseManager = new DataManager<>();

    public static void main(String[] args) {
        MongoDBConnector.connect();
        System.out.println("-----MENTOR MATE PORTAL-----");

        Scanner sc = new Scanner(System.in);
        int choice;

        // Populate initial data from MongoDB
        loadInitialData();

        do {
            System.out.println("Select an Option\n" +
                    "1-->Register as Teacher\n" +
                    "2-->Login as Teacher\n" +
                    "3-->Register as Student\n" +
                    "4-->Login as Student\n" +
                    "5-->Exit");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    registerTeacher();
                    break;
                case 2:
                    loginTeacher(sc);
                    break;
                case 3:
                    registerStudent();
                    break;
                case 4:
                    loginStudent(sc);
                    break;
                case 5:
                    System.out.println("Exiting the portal. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }

        } while (choice != 5);

        sc.close();
        MongoDBConnector.disconnect();
    }

    // Method to load initial data into DataManager from MongoDB
    private static void loadInitialData() {
        MongoCollection<Document> studentCollection = MongoDBConnector.getMongoDatabase().getCollection("Student");
        MongoCollection<Document> teacherCollection = MongoDBConnector.getMongoDatabase().getCollection("Teacher");
        MongoCollection<Document> coursesCollection = MongoDBConnector.getMongoDatabase().getCollection("Courses");

        // Load students
        for (Document doc : studentCollection.find()) {
            Student student = Student.fromDocument(doc);
            studentManager.addItem(student);
        }

        // Load teachers
        for (Document doc : teacherCollection.find()) {
            Teacher teacher = Teacher.fromDocument(doc);
            teacherManager.addItem(teacher);
        }

        // Load courses
        for (Document doc : coursesCollection.find()) {
            Course course = Course.fromDocument(doc);
            courseManager.addItem(course);
        }
    }

    public static void registerTeacher() {
        System.out.println("Registering as Teacher...");
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password;

        // Connect to MongoDB
        MongoCollection<Document> teacherCollection = MongoDBConnector.getMongoDatabase().getCollection("Teacher");
        MongoCollection<Document> coursesCollection = MongoDBConnector.getMongoDatabase().getCollection("Courses");

        // Check for unique username
        boolean isUnique = false;
        while (!isUnique) {
            System.out.println("Enter your Username:");
            username = sc.nextLine();

            // Check if username already exists in the collection
            Document existingTeacher = teacherCollection.find(Filters.eq("username", username)).first();
            if (existingTeacher != null) {
                System.out.println("Username already exists. Please enter a unique username.");
            } else {
                isUnique = true;
            }
        }

        System.out.println("Enter your Password:");
        password = sc.nextLine();

        // Create a new Teacher object
        Teacher newTeacher = new Teacher(username, password);

        // Get the courses the teacher teaches
        System.out.println("Enter the Number of courses you Teach:");
        int n = sc.nextInt();
        sc.nextLine(); // Consume the newline

        for (int i = 0; i < n; i++) {
            String courseID, courseName;
            System.out.println("Enter Course ID:");
            courseID = sc.nextLine();
            System.out.println("Enter Course Name:");
            courseName = sc.nextLine();

            // Add the course to the teacher's course list
            newTeacher.addCourse(courseID, courseName);

            // Insert course in Courses collection
            Document newCourse = new Document("courseID", courseID)
                    .append("courseName", courseName)
                    .append("teacherID", username)
                    .append("studentUsernames", new ArrayList<String>()); // Initialize with an empty list of student usernames
            coursesCollection.insertOne(newCourse);
        }

        // Insert teacher into the Teacher collection
        teacherCollection.insertOne(newTeacher.toDocument());
        System.out.println("YOU HAVE BEEN REGISTERED SUCCESSFULLY");

    }

    public static void loginTeacher(Scanner sc) {
        boolean authenticated = false;
        sc.nextLine();

        MongoCollection<Document> teacherCollection = MongoDBConnector.getMongoDatabase().getCollection("Teacher");

        String username = "";
        String password;

        boolean exists = false;
        while (!exists) {
            System.out.println("Enter your Username:");
            username = sc.nextLine();

            // Check if username exists
            Document existingTeacher = teacherCollection.find(Filters.eq("username", username)).first();
            if (existingTeacher != null) {
                exists = true;
            } else {
                System.out.println("USERNAME NOT FOUND\nPlease register if you have not already.");
                System.out.println("Press 1 to try again or 0 to go back to the main menu.");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline
                if (choice == 0) {
                    return; // Exit and go back to the main menu
                }
            }
        }

        while (!authenticated) {
            System.out.println("Enter your Password:");
            password = sc.nextLine();

            // Retrieve the stored password from MongoDB
            Document existingTeacher = teacherCollection.find(Filters.eq("username", username)).first();
            String storedPassword = existingTeacher.getString("password");

            // Check if password matches
            if (storedPassword.equals(password)) {
                authenticated = true;
                System.out.println("Login successful! Welcome, " + username);

                // Create a Teacher object from the retrieved data
                Teacher teacher = Teacher.fromDocument(existingTeacher);

                // Pass the Teacher object to the teacherMenu main method
                teacherMenu.Main(teacher,sc);

            } else {
                System.out.println("Invalid password. Please try again.");
                System.out.println("Press 1 to try again or 0 to go back to the main menu.");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline
                if (choice == 0) {
                    return; // Exit and go back to the main menu
                }
            }
        }
    }

    public static void registerStudent(){

            System.out.println("Registering as Student...");
            Scanner sc = new Scanner(System.in);
            String username = "";
            String password;

            // Connect to MongoDB
            MongoCollection<Document> studentCollection = MongoDBConnector.getMongoDatabase().getCollection("Student");
            MongoCollection<Document> coursesCollection = MongoDBConnector.getMongoDatabase().getCollection("Courses");
            MongoCollection<Document> teacherCollection = MongoDBConnector.getMongoDatabase().getCollection("Teacher");

            // Check for unique username
            boolean isUnique = false;
            while (!isUnique) {
                System.out.println("Enter your Username:");
                username = sc.nextLine();

                // Check if username already exists in the collection
                Document existingStudent = studentCollection.find(Filters.eq("username", username)).first();
                if (existingStudent != null) {
                    System.out.println("Username already exists. Please enter a unique username.");
                } else {
                    isUnique = true;
                }
            }

            System.out.println("Enter your Password:");
            password = sc.nextLine();

            // Create new student object
            Student newStudent = new Student(username, password);

            // Fetch available courses and display them
            List<Document> courseDocs = coursesCollection.find().into(new ArrayList<>());
            System.out.println("Available Courses:");

            for (Document courseDoc : courseDocs) {
                String courseID = courseDoc.getString("courseID");
                String courseName = courseDoc.getString("courseName");
                String teacherID = courseDoc.getString("teacherID");
                System.out.println("Course ID: " + courseID + ", Course Name: " + courseName + ", Teacher: " + teacherID);
            }

            // Ask the student how many courses they want to enroll in
            System.out.println("Enter the number of courses you want to enroll in:");
            int numberOfCourses = sc.nextInt();
            sc.nextLine(); // Consume the newline

            for (int i = 0; i < numberOfCourses; i++) {
                System.out.println("Enter the Course ID and Teacher Username (separated by a space):");
                String input = sc.nextLine();
                String[] parts = input.split(" ");

                if (parts.length != 2) {
                    System.out.println("Invalid input. Please enter both Course ID and Teacher Username.");
                    i--; // Repeat this iteration
                    continue;
                }

                String courseID = parts[0];
                String teacherUsername = parts[1];

                // Retrieve the selected course document from the courses collection
                Document courseDoc = coursesCollection.find(Filters.and(Filters.eq("courseID", courseID), Filters.eq("teacherID", teacherUsername))).first();
                if (courseDoc != null) {
                    String courseName = courseDoc.getString("courseName");

                    // Create a Course object with the complete information
                    Course selectedCourse = new Course(courseID, courseName, teacherUsername);

                    // Add the course to the student's course list
                    newStudent.getCourses().add(selectedCourse);

                    // Update the course's student list in the Courses collection
                    coursesCollection.updateOne(
                            Filters.and(Filters.eq("courseID", courseID), Filters.eq("teacherID", teacherUsername)),
                            Updates.addToSet("studentUsernames", username)
                    );

                    // Update the teacher's course list in the Teacher collection
                    teacherCollection.updateOne(
                            Filters.and(Filters.eq("username", teacherUsername), Filters.eq("courses.courseID", courseID)),
                            Updates.addToSet("courses.$.studentUsernames", username)
                    );

                } else {
                    System.out.println("Course with the specified ID and Teacher Username not found.");
                    i--; // Repeat this iteration if invalid course selection
                }
            }

            // Insert the new student into the Student collection
            studentCollection.insertOne(newStudent.toDocument());
            System.out.println("YOU HAVE BEEN REGISTERED SUCCESSFULLY");
        }

    public static void loginStudent(Scanner sc){
        boolean authenticated = false;
        sc.nextLine();


        // Connect to MongoDB
        MongoCollection<Document> studentCollection = MongoDBConnector.getMongoDatabase().getCollection("Student");

        String username = "";
        String password;

        boolean exists = false;
        Document existingStudent = null;

        // Loop to check if the username exists
        while (!exists) {
            System.out.println("Enter your Username:");
            username = sc.nextLine();

            // Check if username already exists in the collection
            existingStudent = studentCollection.find(Filters.eq("username", username)).first();
            if (existingStudent != null) {
                exists = true;
            } else {
                System.out.println("USERNAME NOT FOUND\nPlease register if you have not already.");
                System.out.println("Press 1 to try again or 0 to go back to the main menu.");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline
                if (choice == 0) {
                    return; // Exit the login method and go back to the main menu
                }
            }
        }

        // Loop to check if the password matches
        while (!authenticated) {
            System.out.println("Enter your Password:");
            password = sc.nextLine();

            // Retrieve the stored password from the database
            String storedPassword = existingStudent.getString("password");

            // Check if password matches
            if (storedPassword.equals(password)) {
                authenticated = true;
                System.out.println("Login successful! Welcome, " + username);

                // Create the Student object using the fromDocument method
                Student student = Student.fromDocument(existingStudent);

                // Pass the student object to the studentMenu
                studentMenu.main(student,sc);
                //TestStudentLogin.main(student);


            } else {
                System.out.println("Invalid password. Please try again.");
                System.out.println("Press 1 to try again or 0 to go back to the main menu.");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline
                if (choice == 0) {
                    return; // Exit the login method and go back to the main menu
                }
            }
        }

        }
}
