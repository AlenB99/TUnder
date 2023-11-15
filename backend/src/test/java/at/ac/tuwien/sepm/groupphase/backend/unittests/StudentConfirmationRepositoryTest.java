package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import at.ac.tuwien.sepm.groupphase.backend.entity.StudentConfirmation;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentConfirmationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StudentConfirmationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentConfirmationRepository studentConfirmationRepository;

    @Test
    public void whenSaveAndRetrieveConfirmation_thenShouldMatch() {
        // given
        Student student = new Student(); // Setup student object as required

        StudentConfirmation confirmation = new StudentConfirmation();
        confirmation.setConfirmationToken("123456");
        confirmation.setExpirationTime(LocalDateTime.now());
        confirmation.setStudent(student);

        entityManager.persistAndFlush(student);
        entityManager.persistAndFlush(confirmation);

        // when
        StudentConfirmation found = studentConfirmationRepository.findByConfirmationToken(confirmation.getConfirmationToken());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getConfirmationToken()).isEqualTo(confirmation.getConfirmationToken());
        assertThat(found.getExpirationTime()).isEqualTo(confirmation.getExpirationTime());
        assertThat(found.getStudent()).isEqualTo(confirmation.getStudent());
    }
}
