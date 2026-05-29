package com.devops.studentapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.devops.studentapi.model.Student;

// @Service marks this class as a Spring-managed service bean
// It holds the business logic and in-memory data store

@Service
public class StudentService {

    // AtomicLong is thread-safe counter for generating unique IDs
    private final AtomicLong counter = new AtomicLong(1);

    // In-memory list (no database needed for this demo)
    private final List<Student> students = new ArrayList<>();

    // Pre-load some sample students when the app starts
    public StudentService() {
        students.add(new Student(counter.getAndIncrement(), "Arjun Sharma",   "arjun@example.com",  2, "CSE"));
        students.add(new Student(counter.getAndIncrement(), "Priya Patel",    "priya@example.com",  3, "ECE"));
        students.add(new Student(counter.getAndIncrement(), "Rahul Singh",    "rahul@example.com",  1, "ME"));
        students.add(new Student(counter.getAndIncrement(), "Rajat", "simran@lpu.in", 3, "CSE"));
    }

    // GET all students
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // GET one student by ID
    public Optional<Student> getStudentById(Long id) {
        return students.stream()
                       .filter(s -> s.getId().equals(id))
                       .findFirst();
    }

    // POST - add a new student
    public Student addStudent(Student student) {
        student.setId(counter.getAndIncrement());
        students.add(student);
        return student;
    }

    // PUT - update an existing student
    public Optional<Student> updateStudent(Long id, Student updated) {
        return students.stream()
                       .filter(s -> s.getId().equals(id))
                       .findFirst()
                       .map(s -> {
                           s.setName(updated.getName());
                           s.setEmail(updated.getEmail());
                           s.setYear(updated.getYear());
                           s.setBranch(updated.getBranch());
                           return s;
                       });
    }

    // DELETE - remove a student
    public boolean deleteStudent(Long id) {
        return students.removeIf(s -> s.getId().equals(id));
    }
}
