package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.FilterSpecifications;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class GroupRepositoryTest {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    GroupRelationshipRepository groupRelationshipRepository;

    @Autowired
    LvaRepository lvaRepository;

    @Test
    public void getFiltered_returnEmptyList() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        var groupMember = groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(student).build());
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withMembers(List.of(groupMember)).build());
        groupMember.setStudyGroup(group);
        groupMember = groupMemberRepository.save(groupMember);
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertTrue(result.isEmpty());
    }

    @Test
    public void getFiltered_noFilter_getGroups() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        var groupMember = groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(student).build());
        var groupWithStudent = groupRepository.save(Group.GroupBuilder.aGroup().withMembers(List.of(groupMember)).build());
        groupMember.setStudyGroup(groupWithStudent);
        groupMember = groupMemberRepository.save(groupMember);
        var group = groupRepository.save(Group.GroupBuilder.aGroup().build());
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(1, result.size());
        assertEquals(group,result.get(0));
    }

    @Test
    public void getFiltered_notIncludeAlreadyInRelationship() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        var group = groupRepository.save(Group.GroupBuilder.aGroup().build());
        var group1 = groupRepository.save(Group.GroupBuilder.aGroup().build());
        var rel = new GroupRelationship();
        rel.setRecommendedGroup(group1);
        rel.setUser(student);
        rel.setStatus(RelStatus.LIKED);
        groupRelationshipRepository.save(rel);
        var all = groupRepository.findAll();
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(2, all.size());
        assertEquals(1, result.size());
        assertEquals(group,result.get(0));
    }

    @Test
    public void getFiltered_filterMeetsIrl_getGroups() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        groupRepository.save(Group.GroupBuilder.aGroup().withMeetsIrl(false).build());
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withMeetsIrl(true).build());
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().withMeetsIrl(true).build(), student.getId()));
        assertEquals(1, result.size());
        assertEquals(group,result.get(0));
    }

    @Test
    public void getFiltered_filterPrefLanguage_getGroups() {
        Language language = languageRepository.save(new Language("TestLanguage", "TestLanguage"));
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        groupRepository.save(Group.GroupBuilder.aGroup().build());
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withPrefLanguage(language).build());
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().withPrefLanguage(language).build(), student.getId()));
        assertEquals(1, result.size());
        assertEquals(group,result.get(0));
    }

    @Test
    public void getFiltered_filterLVA_getGroups() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().build());
        var lva1 = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("1L").build());
        var lva2 = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("2L").build());
        groupRepository.save(Group.GroupBuilder.aGroup().withLvas(List.of(lva1)).build());
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withLvas(List.of(lva1, lva2)).build());
        var result = groupRepository.findAll(FilterSpecifications.
            filterSpecificatonsGroup(Filter.FilterBuilder.aFilter().withLvas(List.of(lva2)).build(), student.getId()));
        assertEquals(1, result.size());
        assertEquals(group,result.get(0));
    }

    @Test
    public void getGroupByName_returnsCorrectGroup() {
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withName("getGroupByName").build());
        assertNotNull(group);
        assertTrue(group.getId() > 0);

        var queried = groupRepository.getGroupByName("getGroupByName");
        assertEquals(group, queried);
    }

    @Test
    public void getGroupById_returnsCorrectGroup() {
        var group = groupRepository.save(Group.GroupBuilder.aGroup().withName("getGroupById").build());
        assertNotNull(group);
        assertTrue(group.getId() > 0);

        var queried = groupRepository.getGroupById(group.getId());
        assertEquals(group, queried);
    }

    @Test
    public void findAll_returnsAllGroups() {
        List<Group> groups = Arrays.stream(new Group[] {
            groupRepository.save(Group.GroupBuilder.aGroup().withName("Group1").build()),
            groupRepository.save(Group.GroupBuilder.aGroup().withName("Group2").build()),
            groupRepository.save(Group.GroupBuilder.aGroup().withName("Group3").build()),
        }).toList();
        var all = groupRepository.findAll();
        assertEquals(groups, all);
    }

}
