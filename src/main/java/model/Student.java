package com.devops.studentapi.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

// This is a plain Java class (POJO) that represents a Student
// No database is used — data is stored in memory for simplicity

public class Student {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Min(value = 1, message = "Branch year must be at least 1")
    @Max(value = 4, message = "Branch year cannot exceed 4")
    private int year;

    @NotBlank(message = "Branch cannot be empty")
    private String branch;

    // Default constructor (required by Spring/Jackson for JSON parsing)
    public Student() {}

    // All-args constructor
    public Student(Long id, String name, String email, int year, String branch) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.year  = year;
        this.branch = branch;
    }

    // Getters and Setters
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public int getYear()             { return year; }
    public void setYear(int year)    { this.year = year; }

    public String getBranch()              { return branch; }
    public void setBranch(String branch)   { this.branch = branch; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email +
               "', year=" + year + ", branch='" + branch + "'}";
    }
}
