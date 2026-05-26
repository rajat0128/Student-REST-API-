package com.devops.studentapi.controller;

import com.devops.studentapi.model.Student;
import com.devops.studentapi.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Every method automatically converts return value to JSON

@RestController
@RequestMapping("/api/students")   // All endpoints start with /api/students
public class StudentController {

    // Spring automatically injects StudentService (Dependency Injection)
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── GET /api/students ─────────────────────────────────────────────────────
    // Returns all students as a JSON array
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ── GET /api/students/{id} ────────────────────────────────────────────────
    // Returns one student, or 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)                          // 200 OK
                .orElse(ResponseEntity.notFound().build());       // 404
    }

    // ── POST /api/students ────────────────────────────────────────────────────
    // Creates a new student; @Valid triggers @NotBlank / @Email checks
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student created = studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201
    }

    // ── PUT /api/students/{id} ────────────────────────────────────────────────
    // Updates an existing student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                  @Valid @RequestBody Student student) {
        return studentService.updateStudent(id, student)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE /api/students/{id} ─────────────────────────────────────────────
    // Deletes a student; returns 204 No Content on success
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentService.deleteStudent(id)) {
            return ResponseEntity.noContent().build();   // 204
        }
        return ResponseEntity.notFound().build();         // 404
    }
}
