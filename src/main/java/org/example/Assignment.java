package org.example;

public class Assignment {
    private String courseID;
    private String assignmentDesc;
    private String dueDate;

    // Constructor
    public Assignment(String courseID, String assignmentDesc, String dueDate) {
        this.courseID = courseID;
        this.assignmentDesc = assignmentDesc;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getAssignmentDesc() {
        return assignmentDesc;
    }

    public void setAssignmentDesc(String assignmentDesc) {
        this.assignmentDesc = assignmentDesc;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
