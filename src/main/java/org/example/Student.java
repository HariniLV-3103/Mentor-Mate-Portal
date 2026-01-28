package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    private String username;
    private String password;
    private List<Course> courses;
    private Map<String, Map<String, Integer>> attendance;  // courseID -> {date -> present(1)/absent(0)}
    private ToDoList todo;
    private Assignments assignments;

    // Constructor
    public Student(String username, String password) {
        this.username = username;
        this.password = password;
        this.courses = new ArrayList<>();  // Initialize empty course list
        this.attendance = new HashMap<>();  // Initialize empty attendance map
        this.todo = new ToDoList();         // Initialize empty ToDoList
        this.assignments = new Assignments(); // Initialize empty Assignments list
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Map<String, Map<String, Integer>> getAttendance() {
        return attendance;
    }

    public void setAttendance(Map<String, Map<String, Integer>> attendance) {
        this.attendance = attendance;
    }

    public ToDoList getToDoList() {
        return todo;
    }

    public void setTodoList(ToDoList todo) {
        this.todo = todo;
    }

    public Assignments getAssignments() {
        return assignments;
    }

    public void setAssignments(Assignments assignments) {
        this.assignments = assignments;
    }

    public Document toDocument() {
        // Convert attendance map to a Document
        Document attendanceDoc = new Document();
        for (Map.Entry<String, Map<String, Integer>> entry : attendance.entrySet()) {
            String courseId = entry.getKey();
            Map<String, Integer> dateAttendance = entry.getValue();

            // Create a new map to convert Integer values to Object
            Map<String, Object> dateAttendanceObj = new HashMap<>();
            for (Map.Entry<String, Integer> dateEntry : dateAttendance.entrySet()) {
                dateAttendanceObj.put(dateEntry.getKey(), dateEntry.getValue());
            }

            // Append the courseId with the newly created Document
            attendanceDoc.append(courseId, new Document(dateAttendanceObj));
        }

        // Create the main Document for the Student
        Document studentDoc = new Document("username", this.username)
                .append("password", this.password)
                .append("attendance", attendanceDoc)
                .append("courses", getCoursesDocument())
                .append("todo", todo.toDocument())
                .append("assignments", assignments.toDocument());

        return studentDoc;
    }

    // Method to convert courses to Document format
    private List<Document> getCoursesDocument() {
        List<Document> courseDocs = new ArrayList<>();
        for (Course course : courses) {
            courseDocs.add(new Document("courseID", course.getCourseID())
                    .append("courseName", course.getCourseName())
                    .append("teacherID", course.getTeacherID()));
        }
        return courseDocs;
    }

    public static Student fromDocument(Document doc) {
        String username = doc.getString("username");
        String password = doc.getString("password");

        // Initialize ToDoList and Assignments from stored data
        ToDoList todo = ToDoList.fromDocument(doc.get("todo", Document.class));
        Assignments assignments = Assignments.fromDocument(doc.get("assignments", Document.class));

        // Initialize Courses list
        List<Course> courses = new ArrayList<>();
        List<Document> courseDocs = (List<Document>) doc.get("courses");
        if (courseDocs != null) {
            for (Document courseDoc : courseDocs) {
                String courseID = courseDoc.getString("courseID");
                String courseName = courseDoc.getString("courseName");
                String teacherID = courseDoc.getString("teacherID");
                Course course = new Course(courseID, courseName, teacherID);
                courses.add(course);
            }
        }

        // Initialize attendance map
        Map<String, Map<String, Integer>> attendance = new HashMap<>();
        Document attendanceDoc = doc.get("attendance", Document.class);
        if (attendanceDoc != null) {
            for (String courseID : attendanceDoc.keySet()) {
                Document datesDoc = attendanceDoc.get(courseID, Document.class);
                Map<String, Integer> datesMap = new HashMap<>();
                for (String date : datesDoc.keySet()) {
                    datesMap.put(date, datesDoc.getInteger(date));
                }
                attendance.put(courseID, datesMap);
            }
        }

        // Create and return the populated Student object
        Student student = new Student(username, password);
        student.setCourses(courses);
        student.setAttendance(attendance);
        student.setTodoList(todo);
        student.setAssignments(assignments);
        return student;
    }

    public static List<Student> getStudentsByCourse(String courseID) {
        // Connect to MongoDB
        MongoCollection<Document> studentCollection = MongoDBConnector.getStudentCollection();

        // Find students with the specified courseID in their course list
        List<Student> students = new ArrayList<>();
        for (Document doc : studentCollection.find(Filters.elemMatch("courses", Filters.eq("courseID", courseID)))) {
            students.add(fromDocument(doc)); // Convert document to Student object
        }

        return students;
    }

    public static void updateStudentInDatabase(Student student) {
        // Connect to MongoDB
        MongoCollection<Document> studentCollection = MongoDBConnector.getStudentCollection();

        // Convert the Student object to a Document
        Document updatedStudentDoc = student.toDocument(); // Assuming you have a toDocument method in your Student class

        // Update the existing student record based on the username
        studentCollection.replaceOne(Filters.eq("username", student.getUsername()), updatedStudentDoc);

        System.out.println("Student record updated successfully in the database.");
    }

    public void updateAttendanceInDatabase(String courseId, String date, int status) {
        // Connect to MongoDB
        MongoCollection<Document> attendanceCollection = MongoDBConnector.getAttendanceCollection();

        // Create a Document for the attendance entry
        Document attendanceDoc = new Document("studentId", this.username)  // Use the username as studentId
                .append("courseId", courseId)
                .append("date", date)
                .append("status", status);

        // Insert or update attendance record
        attendanceCollection.updateOne(
                Filters.and(Filters.eq("studentId", this.username), Filters.eq("courseId", courseId), Filters.eq("date", date)),
                new Document("$set", attendanceDoc),
                new com.mongodb.client.model.UpdateOptions().upsert(true) // Create new if it doesn't exist
        );

        System.out.println("Attendance updated successfully for " + this.username + " in " + courseId + " on " + date + ".");
    }
}
