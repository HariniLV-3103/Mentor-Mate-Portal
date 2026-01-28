package org.example;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Assignments {
    private List<Assignment> notCompleted;
    private List<Assignment> completed;

    // Constructor
    public Assignments() {
        this.notCompleted = new ArrayList<>();
        this.completed = new ArrayList<>();
    }

    // Getters and Setters
    public List<Assignment> getNotCompleted() {
        return notCompleted;
    }

    public void setNotCompleted(List<Assignment> notCompleted) {
        this.notCompleted = notCompleted;
    }

    public List<Assignment> getCompleted() {
        return completed;
    }

    public void setCompleted(List<Assignment> completed) {
        this.completed = completed;
    }

    // Methods to manage assignments
    public void addAssignment(Assignment assignment) {
        notCompleted.add(assignment);
    }

    public void completeAssignment(Assignment assignment) {
        notCompleted.remove(assignment);
        completed.add(assignment);
    }

    public Document toDocument() {
        List<Document> notCompletedDocs = new ArrayList<>();
        for (Assignment assignment : notCompleted) {
            notCompletedDocs.add(new Document("courseID", assignment.getCourseID())
                    .append("assignmentDesc", assignment.getAssignmentDesc())
                    .append("dueDate", assignment.getDueDate()));
        }

        List<Document> completedDocs = new ArrayList<>();
        for (Assignment assignment : completed) {
            completedDocs.add(new Document("courseID", assignment.getCourseID())
                    .append("assignmentDesc", assignment.getAssignmentDesc())
                    .append("dueDate", assignment.getDueDate()));
        }

        return new Document("notCompleted", notCompletedDocs)
                .append("completed", completedDocs);
    }

    public static Assignments fromDocument(Document doc) {
        Assignments assignments = new Assignments();

        List<Document> notCompletedDocs = (List<Document>) doc.get("notCompleted");
        if (notCompletedDocs != null) {
            for (Document assignmentDoc : notCompletedDocs) {
                Assignment assignment = new Assignment(assignmentDoc.getString("courseID"),
                        assignmentDoc.getString("assignmentDesc"),
                        assignmentDoc.getString("dueDate"));
                assignments.getNotCompleted().add(assignment);
            }
        }

        List<Document> completedDocs = (List<Document>) doc.get("completed");
        if (completedDocs != null) {
            for (Document assignmentDoc : completedDocs) {
                Assignment assignment = new Assignment(assignmentDoc.getString("courseID"),
                        assignmentDoc.getString("assignmentDesc"),
                        assignmentDoc.getString("dueDate"));
                assignments.getCompleted().add(assignment);
            }
        }
        return assignments;
    }


}
