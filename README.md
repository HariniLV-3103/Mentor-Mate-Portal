# Student-Teacher Academic Management System

## Introduction
The Student-Teacher Academic Management System is a Java-based application designed to streamline academic management for both teachers and students. The system leverages MongoDB as its database to manage data related to students, teachers, courses, attendance, assignments, and more.

This project encapsulates key Object-Oriented Programming (OOP) principles such as Encapsulation, Abstraction, Polymorphism, and Association, making it a robust solution for managing academic activities.

## Features

### For Students
- **Course Enrollment**: View courses they are enrolled in.
- **Attendance Management**: Mark and view attendance.
- **Assignments**: View pending assignments with due dates.
- **To-Do List**: Manage personal tasks.

### For Teachers
- **Course Management**: View and manage the courses they teach.
- **Post Assignments**: Assign tasks to students with specific due dates.
- **Attendance Tracking**: Manage student attendance.
- **Dashboard**: Comprehensive view of all their courses and assignments.

## Technologies Used
- **Java**: Core programming language.
- **MongoDB**: NoSQL database for storing data.
- **Java OOP Concepts**: Encapsulation, Abstraction, Polymorphism, Association.
- **Maven**: Dependency management.

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── org/example/
│   │       ├── Main.java
│   │       ├── MongoDBConnector.java
│   │       ├── Student.java
│   │       ├── Teacher.java
│   │       ├── Course.java
│   │       ├── Assignment.java
│   │       ├── ToDoList.java
│   │       ├── Attendance.java
│   │       └── teacherMenu.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## Class Breakdown
- **Student**: Handles student-specific data and operations.
- **Teacher**: Manages teacher-specific actions like posting assignments.
- **Course**: Represents the courses with IDs, names, and associated students.
- **Assignment**: Represents assignments with descriptions and due dates.
- **ToDoList**: Handles personal tasks for students.
- **Attendance**: Manages attendance records.
- **MongoDBConnector**: Manages MongoDB connection and CRUD operations.
- **teacherMenu**: Provides an interactive menu for teachers.

## Setup and Installation

### Prerequisites
- Java JDK (version 8 or higher)
- Maven (for managing dependencies)
- MongoDB (installed locally or using MongoDB Atlas)

### Usage

#### For Teachers
1. Login using your credentials.
2. Access the Teacher Dashboard.
3. Select options to post assignments or manage attendance.
4. Logout after completing your tasks.

#### For Students
1. Login using your credentials.
2. Access the Student Dashboard.
3. View your courses, assignments, and to-do list.
4. Mark attendance and manage personal tasks.

## Database Schema

### Student Collection
```json
{
  "username": "john_doe",
  "password": "hashed_password",
  "courses": [
    {"courseID": "CS101", "courseName": "Data Structures", "teacherID": "teacher123"}
  ],
  "attendance": {
    "CS101": {"2024-11-01": 1, "2024-11-02": 0}
  },
  "todo": {"tasks": ["Prepare for midterms"]},
  "assignments": {
    "notCompleted": [{"courseID": "CS101", "assignmentDesc": "Project Report", "dueDate": "2024-11-20"}]
  }
}
```

### Teacher Collection
```json
{
  "username": "teacher123",
  "password": "hashed_password",
  "courses": [
    {"courseID": "CS101", "courseName": "Data Structures", "studentUsernames": ["john_doe", "jane_smith"]}
  ]
}
```

