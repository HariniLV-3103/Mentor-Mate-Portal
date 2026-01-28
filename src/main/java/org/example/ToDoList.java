package org.example;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class ToDoList {
    private List<Task> notCompleted;
    private List<Task> completed;

    // Constructor
    public ToDoList() {
        this.notCompleted = new ArrayList<>();
        this.completed = new ArrayList<>();
    }

    // Getters and Setters
    public List<Task> getNotCompleted() {
        return notCompleted;
    }

    public void setNotCompleted(List<Task> notCompleted) {
        this.notCompleted = notCompleted;
    }

    public List<Task> getCompleted() {
        return completed;
    }

    public void setCompleted(List<Task> completed) {
        this.completed = completed;
    }

    // Methods to add tasks
    public void addTask(Task task) {
        notCompleted.add(task);
    }

    public void completeTask(Task task) {
        notCompleted.remove(task);
        completed.add(task);
    }

    public Document toDocument() {
        List<Document> notCompletedDocs = new ArrayList<>();
        for (Task task : notCompleted) {
            notCompletedDocs.add(new Document("taskNo", task.getTaskNo())
                    .append("taskDesc", task.getTaskDesc())
                    .append("dueDate", task.getDueDate()));
        }

        List<Document> completedDocs = new ArrayList<>();
        for (Task task : completed) {
            completedDocs.add(new Document("taskNo", task.getTaskNo())
                    .append("taskDesc", task.getTaskDesc())
                    .append("dueDate", task.getDueDate()));
        }

        return new Document("notCompleted", notCompletedDocs)
                .append("completed", completedDocs);
    }

    public static ToDoList fromDocument(Document doc) {
        ToDoList todoList = new ToDoList();

        List<Document> notCompletedDocs = (List<Document>) doc.get("notCompleted");
        if (notCompletedDocs != null) {
            for (Document taskDoc : notCompletedDocs) {
                Task task = new Task(taskDoc.getInteger("taskNo"),
                        taskDoc.getString("taskDesc"),
                        taskDoc.getString("dueDate"));
                todoList.getNotCompleted().add(task);
            }
        }

        List<Document> completedDocs = (List<Document>) doc.get("completed");
        if (completedDocs != null) {
            for (Document taskDoc : completedDocs) {
                Task task = new Task(taskDoc.getInteger("taskNo"),
                        taskDoc.getString("taskDesc"),
                        taskDoc.getString("dueDate"));
                todoList.getCompleted().add(task);
            }
        }
        return todoList;
    }
}
