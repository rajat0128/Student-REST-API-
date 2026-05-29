package com.devops.studentapi;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devops.studentapi.model.Student;
import com.devops.studentapi.service.StudentService;

// Unit tests are run automatically by Maven during the 'test' phase
// Jenkins will run these and fail the pipeline if any test breaks

class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        // Fresh service instance before each test
        studentService = new StudentService();
    }

    @Test
    void shouldReturnAllStudents() {
        List<Student> students = studentService.getAllStudents();
        // Pre-loaded with 3 students in constructor
        assertEquals(4, students.size(), "Should start with 3 pre-loaded students");
    }

    @Test
    void shouldFindStudentById() {
        Optional<Student> student = studentService.getStudentById(1L);
        assertTrue(student.isPresent(), "Student with ID 1 should exist");
        assertEquals("Arjun Sharma", student.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenStudentNotFound() {
        Optional<Student> student = studentService.getStudentById(999L);
        assertFalse(student.isPresent(), "Non-existent student should return empty");
    }

    @Test
    void shouldAddNewStudent() {
        Student newStudent = new Student(null, "Test User", "test@example.com", 2, "CSE");
        Student saved = studentService.addStudent(newStudent);

        assertNotNull(saved.getId(), "ID should be auto-assigned");
        assertEquals("Test User", saved.getName());
        assertEquals(5, studentService.getAllStudents().size());
    }

    @Test
    void shouldDeleteStudent() {
        boolean deleted = studentService.deleteStudent(1L);
        assertTrue(deleted, "Deletion should succeed for existing student");
        assertEquals(3, studentService.getAllStudents().size());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentStudent() {
        boolean deleted = studentService.deleteStudent(999L);
        assertFalse(deleted, "Deletion of non-existent student should return false");
    }
}
