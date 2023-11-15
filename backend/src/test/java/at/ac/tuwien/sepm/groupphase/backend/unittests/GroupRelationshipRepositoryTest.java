package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

    import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GroupRelationshipRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRelationshipRepository groupRelationshipRepository;

    @Test
    public void whenExistsByUserAndRecommended_thenReturnTrue() {
        // given
        Student student = new Student();
        Group group = new Group();
        entityManager.persistAndFlush(student);
        entityManager.persistAndFlush(group);
        GroupRelationship groupRelationship = new GroupRelationship(student, group, RelStatus.LIKED);
        entityManager.persistAndFlush(groupRelationship);

        // when
        boolean found = groupRelationshipRepository.existsByUserAndRecommendedGroup(student, group);

        // then
        assertThat(found).isEqualTo(true);
    }

    @Test
    public void whenNotExistsByUserAndRecommended_thenReturnFalse() {
        // given
        Student student = new Student();
        Group group = new Group();
        entityManager.persistAndFlush(student);
        entityManager.persistAndFlush(group);

        // when
        boolean found = groupRelationshipRepository.existsByUserAndRecommendedGroup(student, group);

        // then
        assertThat(found).isEqualTo(false);
    }

    @Test
    public void whenSaveGroupRelationship_thenItIsPersisted() {
        // given
        Student student = new Student();
        Group group = new Group();
        GroupRelationship groupRelationship = new GroupRelationship(student, group, RelStatus.LIKED);

        // when
        groupRelationshipRepository.save(groupRelationship);

        // then
        GroupRelationship found = entityManager.find(GroupRelationship.class, groupRelationship.getId());
        assertThat(found).isEqualTo(groupRelationship);
    }

}
