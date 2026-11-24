package lk.ousl.api.demo;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/api/students")
public class StudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }

    private final StudentRepository repository;

    public StudentApplication(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Student> getStudents() {
        return repository.findAll();
    }

    @PostMapping
    public Student addStudent(@Valid @RequestBody Student student) {
        return repository.save(student);
    }
}

@Entity
@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String course;

    public Student() {
    }

    public Student(String name, String course) {
        this.name = name;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}

interface StudentRepository extends JpaRepository<Student, Long> {
}
