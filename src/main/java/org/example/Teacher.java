package org.example;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String username;
    private String password;
    private List<Course> courses;

    // Constructor
    public Teacher(String username, String password) {
        this.username = username;
        this.password = password;
        this.courses = new ArrayList<>();
    }

    // Add a course to the teacher's course list
    public void addCourse(String courseID, String courseName) {
        this.courses.add(new Course(courseID, courseName, this.username));
    }

    // Convert Teacher object to MongoDB document for storage
    public Document toDocument() {
        List<Document> coursesList = new ArrayList<>();
        for (Course course : courses) {
            // Convert each course into a MongoDB document
            Document courseDoc = new Document("courseID", course.getCourseID())
                    .append("courseName", course.getCourseName())
                    .append("teacherID", course.getTeacherID())
                    .append("studentUsernames", course.getStudentIDs());  // Add the list of student usernames

            coursesList.add(courseDoc);
        }

        return new Document("username", this.username)
                .append("password", this.password)
                .append("courses", coursesList);
    }

    public static Teacher fromDocument(Document doc) {
        String username = doc.getString("username");
        String password = doc.getString("password");

        Teacher teacher = new Teacher(username, password);

        // Load courses from the document
        List<Document> courseDocs = (List<Document>) doc.get("courses");
        for (Document courseDoc : courseDocs) {
            String courseID = courseDoc.getString("courseID");
            String courseName = courseDoc.getString("courseName");
            String teacherID = courseDoc.getString("teacherID");

            // Create the course and add student usernames
            Course course = new Course(courseID, courseName, teacherID);
            List<String> studentUsernames = (List<String>) courseDoc.get("studentUsernames");
            course.setStudentIDs(studentUsernames);

            // Add the course to the teacher's list of courses
            teacher.getCourses().add(course);
        }

        return teacher;
    }
    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Course> getCourses() {
        return courses;
    }

}
