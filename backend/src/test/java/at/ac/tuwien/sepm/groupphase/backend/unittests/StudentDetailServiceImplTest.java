package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.StudentDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudentDetailServiceImplTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentDetailServiceImpl studentService;

    @Test
    public void findStudentByEmail_returnsCorrectStudent() {
        Student student = studentRepository.save(Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2004, 1, 1))
            .withId(1L)
            .withEmail("e12122106@student.tuwien.ac.at")
            .build());
        Student result = studentService.findStudentByEmail("e12122106@student.tuwien.ac.at");
        assertEquals(student.getEmail(), result.getEmail());
        assertEquals(student.getId(), result.getId());
    }

    @Test
    public void findStudentById_returnsCorrectStudent() {
        Student student = studentRepository.save(Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2004, 1, 1))
            .withId(1L)
            .withEmail("e12122106@student.tuwien.ac.at")
            .build());
        Student result = studentService.findStudentById(1L);
        assertEquals(student.getEmail(), result.getEmail());
        assertEquals(student.getId(), result.getId());
    }

    @Test
    public void findStudentById_withBadDataThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            Student student = studentRepository.save(Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2004, 1, 1))
                .withId(1L)
                .withEmail("e12122106@student.tuwien.ac.at")
                .build());
            Student result = studentService.findStudentById(5L);
        });
    }

    @Test
    public void findAll_returnsAllStoredStudents() {
        studentRepository.save(Student.StudentBuilder.aStudent().withLastName("Mayer")
            .withEmail("e12122106@student.tuwien.ac.at").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withLastName("Müller")
            .withEmail("e12122106@student.tuwien.ac.at 0").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withLastName("Huber")
            .withEmail("e12122106@student.tuwien.ac.at 1").build());
        List<Student> studentList = studentService.findAll();
        assertThat(studentList.size()).isGreaterThanOrEqualTo(3);
        assertThat(studentList)
            .map(Student::getLastName, Student::getEmail)
            .contains(tuple("Mayer", "e12122106@student.tuwien.ac.at"),
                tuple("Müller", "e12122106@student.tuwien.ac.at 0"),
                tuple("Huber", "e12122106@student.tuwien.ac.at 1")
            );
    }

    @Test
    public void persistStudent_persistsStudent() {
        Student student = Student.StudentBuilder.aStudent().withLastName("Mayer")
            .withEmail("e12122106@student.tuwien.ac.at").build();
        Student result = studentService.persistStudent(student);
        assertEquals(student,result);
    }

    @Test
    public void persistStudent_withBadDataThrowsException() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            Student student = Student.StudentBuilder.aStudent().withLastName("Mayer")
                .withEmail("e12122106@student.tuwien.ac.at").build();
            Student student2 = Student.StudentBuilder.aStudent().withLastName("Mayer")
                .withEmail("e12122106@student.tuwien.ac.at").build();
            studentService.persistStudent(student);
            studentService.persistStudent(student2);
        });
    }

    @Test
    public void updateStudent_updatesCorrectStudent() throws ValidationException {
        // A very basic JPEG file, for the purpose of test only
        byte[] imageData = new byte[] {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
            // Other bytes representing the image data would go here
            (byte) 0xFF, (byte) 0xD9
        };
        Student originalStudent = studentRepository.save(Student.StudentBuilder.aStudent().withId(1L)
            .withLastName("Mayer").withFirstName("Franz").withPassword("password")
            .withEmail("e12122106@student.tuwien.ac.at").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withLastName("Müller")
            .withFirstName("Franz").withPassword("password").withEmail("e12122107@student.tuwien.ac.at").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withLastName("Huber")
            .withFirstName("Franz").withPassword("password").withEmail("e12122108@student.tuwien.ac.at").build());
        studentService.updateStudent(Student.StudentBuilder.aStudent().withId(1L).withLastName("Mayer")
            .withFirstName("Franz").withPassword("password").withEmail("e12122109@student.tuwien.ac.at").build());
        Student retrievedStudent = studentRepository.findStudentByEmail("e12122109@student.tuwien.ac.at");
        assertEquals(originalStudent.getLastName(), retrievedStudent.getLastName());
        assertEquals(originalStudent.getId(),retrievedStudent.getId());
    }

}
