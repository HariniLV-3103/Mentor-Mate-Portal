package org.example;

public class Task {
    private int taskNo;
    private String taskDesc;
    private String dueDate;

    // Constructor
    public Task(int taskNo, String taskDesc, String dueDate) {
        this.taskNo = taskNo;
        this.taskDesc = taskDesc;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public int getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(int taskNo) {
        this.taskNo = taskNo;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
