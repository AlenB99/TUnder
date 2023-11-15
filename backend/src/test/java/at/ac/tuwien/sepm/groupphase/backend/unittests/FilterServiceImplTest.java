package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FilterRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.FilterServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.FilterValidator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class FilterServiceImplTest {

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LvaService lvaService;

    @Mock
    private FilterValidator filterValidator;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private FilterServiceImpl filterService;

    @Test
    public void setFilter_createsNewFilter() throws ValidationException, ConflictException {
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(17).build();
        Student student = Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2004, 1, 1)).withId(1L).build();
        filter.setStudent(student);

        Filter filterReturn = Filter.FilterBuilder.aFilter().withId(1L).withMinAge(17).build();

        given(filterRepository.save(filter)).willReturn(filterReturn);
        given(filterRepository.findByStudent(student)).willReturn(Optional.empty());

        Filter result = filterService.setFilter(filter);

        assertEquals(filterReturn, result);
        verify(filterValidator, times(1)).validateFilterForSave(filter, filterService, lvaService);
    }

    @Test
    public void setFilter_updateFilterIfAlreadyExistsForStudent() throws ValidationException, ConflictException {
        Filter existingFilter = Filter.FilterBuilder.aFilter().withMinAge(17).withId(1L).build();
        Filter newFilter = Filter.FilterBuilder.aFilter().withMinAge(19).build();
        Student student = Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2004, 1, 1)).withId(1L).build();
        existingFilter.setStudent(student);
        newFilter.setStudent(student);

        given(filterRepository.findByStudent(student)).willReturn(Optional.of(existingFilter));
        given(filterRepository.save(argThat(filter -> filter.getId().equals(existingFilter.getId())))).willAnswer(new Answer<Filter>() {
            @Override
            public Filter answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });

        Filter result = filterService.setFilter(newFilter);

        assertEquals(1L, result.getId());
        assertEquals(newFilter.getMinAge(), result.getMinAge());
    }

    @Test
    public void getFilteredStudents_returnAll_ForNoFilter() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Student student2 = Student.StudentBuilder.aStudent().withId(2L).withDateOfBirth(LocalDate.of(2003, 1, 1)).build();
        Student student3 = Student.StudentBuilder.aStudent().withId(3L).withDateOfBirth(LocalDate.of(2002, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().build();

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.empty());
        given(studentRepository.findAll(any(Specification.class))).willReturn(List.of(student2, student3));

        var result = filterService.getFilteredStudents(student1);

        assertEquals(2, result.size());
        assertTrue(result.contains(student2));
        assertTrue(result.contains(student3));
    }

    @Test
    public void getFilteredStudents_returnFiltered() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Student student2 = Student.StudentBuilder.aStudent().withId(2L).withDateOfBirth(LocalDate.of(2003, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.of(filter));
        given(studentRepository.findAll(any(Specification.class))).willReturn(List.of(student2));

        var result = filterService.getFilteredStudents(student1);

        assertEquals(1, result.size());
        assertTrue(result.contains(student2));
    }

    @Test
    public void getFilteredStudents_getForNonExistentStudent_ThrowNotFoundException() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(false);

        var ex = assertThrows(NotFoundException.class, ()-> filterService.getFilteredStudents(student1));
        assertEquals("Student with id 1 to get filtered recommendations for not found.", ex.getMessage());
    }

    @Test
    public void getFilteredStudents_byId_returnAll_ForNoFilter() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Student student2 = Student.StudentBuilder.aStudent().withId(2L).withDateOfBirth(LocalDate.of(2003, 1, 1)).build();
        Student student3 = Student.StudentBuilder.aStudent().withId(3L).withDateOfBirth(LocalDate.of(2002, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().build();

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.of(filter));
        given(studentRepository.findAll(any(Specification.class))).willReturn(List.of(student2, student3));

        var result = filterService.getFilteredStudents(student1.getId());

        assertEquals(2, result.size());
        assertTrue(result.contains(student2));
        assertTrue(result.contains(student3));
    }

    @Test
    public void getFilteredStudents_byId_returnFiltered() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Student student2 = Student.StudentBuilder.aStudent().withId(2L).withDateOfBirth(LocalDate.of(2003, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.of(filter));
        given(studentRepository.findAll(any(Specification.class))).willReturn(List.of(student2));

        var result = filterService.getFilteredStudents(student1.getId());

        assertEquals(1, result.size());
        assertTrue(result.contains(student2));
    }

    @Test
    public void getFilteredStudents_byId_getForNonExistentStudent_ThrowNotFoundException() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(false);

        var ex = assertThrows(NotFoundException.class, ()-> filterService.getFilteredStudents(student1.getId()));
        assertEquals("Student with id 1 to get filtered recommendations for not found.", ex.getMessage());
    }

    @Test
    public void getFilteredGroups_returnAll_ForNoFilter() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Group group1 = Group.GroupBuilder.aGroup().withId(1L).build();
        Group group2 = Group.GroupBuilder.aGroup().withId(2L).build();
        Group group3 = Group.GroupBuilder.aGroup().withId(3L).build();

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.empty());
        given(groupRepository.findAll(any(Specification.class))).willReturn(List.of(group1, group2, group3));

        var result = filterService.getFilteredGroups(student1);

        assertEquals(3, result.size());
        assertTrue(result.contains(group1));
        assertTrue(result.contains(group2));
        assertTrue(result.contains(group3));
    }

    @Test
    public void getFilteredGroups_returnFiltered() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Group group1 = Group.GroupBuilder.aGroup().withId(1L).withPrefLanguage(new Language("ba", "Bashkir")).build();
        Group group2 = Group.GroupBuilder.aGroup().withId(2L).withPrefLanguage(new Language("av", "Avaric")).build();
        Group group3 = Group.GroupBuilder.aGroup().withId(3L).withPrefLanguage(new Language("bg", "Bulgarian")).build();
        Filter filter = Filter.FilterBuilder.aFilter().withPrefLanguage(new Language("av", "Avaric")).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.of(filter));
        given(groupRepository.findAll(any(Specification.class))).willReturn(List.of(group2));

        var result = filterService.getFilteredGroups(student1);

        assertEquals(1, result.size());
        assertTrue(result.contains(group2));
    }

    @Test
    public void getFilteredGroups_getForNonExistentStudent_ThrowNotFoundException() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(false);

        var ex = assertThrows(NotFoundException.class, ()-> filterService.getFilteredGroups(student1));
        assertEquals("Student with id 1 to get filtered recommendations for not found.", ex.getMessage());
    }

    @Test
    public void getFilteredGroups_byId_returnAll_ForNoFilter() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Group group1 = Group.GroupBuilder.aGroup().withId(1L).build();
        Group group2 = Group.GroupBuilder.aGroup().withId(2L).build();
        Group group3 = Group.GroupBuilder.aGroup().withId(3L).build();

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.empty());
        given(groupRepository.findAll(any(Specification.class))).willReturn(List.of(group1, group2, group3));

        var result = filterService.getFilteredGroups(student1.getId());

        assertEquals(3, result.size());
        assertTrue(result.contains(group1));
        assertTrue(result.contains(group2));
        assertTrue(result.contains(group3));
    }

    @Test
    public void getFilteredGroups_byId_returnFiltered() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Group group1 = Group.GroupBuilder.aGroup().withId(1L).withPrefLanguage(new Language("ba", "Bashkir")).build();
        Group group2 = Group.GroupBuilder.aGroup().withId(2L).withPrefLanguage(new Language("av", "Avaric")).build();
        Group group3 = Group.GroupBuilder.aGroup().withId(3L).withPrefLanguage(new Language("bg", "Bulgarian")).build();
        Filter filter = Filter.FilterBuilder.aFilter().withPrefLanguage(new Language("av", "Avaric")).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(student1.getId()).build()))).willReturn(Optional.of(filter));
        given(groupRepository.findAll(any(Specification.class))).willReturn(List.of(group2));

        var result = filterService.getFilteredGroups(student1.getId());

        assertEquals(1, result.size());
        assertTrue(result.contains(group2));
    }

    @Test
    public void getFilteredGroups_byId_getForNonExistentStudent_ThrowNotFoundException() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1l).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(student1.getId())).willReturn(false);

        var ex = assertThrows(NotFoundException.class, ()-> filterService.getFilteredGroups(student1.getId()));
        assertEquals("Student with id 1 to get filtered recommendations for not found.", ex.getMessage());
    }

    @Test
    public void removeFilter_removesFilter_byStudent() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();

        Filter filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(filterRepository.deleteByStudent(student1)).willReturn(1L);

        filterService.removeFilter(student1);

        verify(filterRepository, times(1)).deleteByStudent(student1);
    }

    @Test
    public void removeFilter_removesFilter_byFilter() throws ValidationException {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        filterService.removeFilter(filter);

        verify(filterRepository, times(1)).delete(filter);
    }

    @Test
    public void removeFilter_removesFilter_byFilterId() {
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        filterService.removeFilter(filter.getId());

        verify(filterRepository, times(1)).deleteById(filter.getId());
    }

    @Test
    public void removeFilter_throwsIllegalArgument_byFilterWithoutId() {
        Filter filter = Filter.FilterBuilder.aFilter().build();
        assertThrows(ValidationException.class, () -> filterService.removeFilter(filter));
    }

    @Test
    public void getFilter_returnFilterForExistingId(){
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).withDateOfBirth(LocalDate.of(2004, 1, 1)).build();
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withMinAge(20).withMaxAge(21).build();
        filter.setStudent(student1);

        given(filterRepository.findById(filter.getId())).willReturn(Optional.of(filter));

        var result = filterService.getFilter(1L);
        assertEquals(filter, result);
    }

    @Test
    public void getFilter_nonExistent_throwsNotFound(){
        given(filterRepository.findById(1L)).willReturn(Optional.empty());

        var ex = assertThrows(NotFoundException.class, ()->filterService.getFilter(1L));
        assertEquals("Filter with id 1 not found", ex.getMessage());
    }

    @Test
    public void getGroupRecomMode_returnsTrue(){
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).build();
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withGroupRecomMode(true).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(1L)).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(1L).build()))).willReturn(Optional.of(filter));

        var result = filterService.getGroupRecomMode(1L);
        assertTrue(result);
    }

    @Test
    public void getGroupRecomMode_returnsFalse(){
        Student student1 = Student.StudentBuilder.aStudent().withId(1L).build();
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withGroupRecomMode(false).build();
        filter.setStudent(student1);

        given(studentRepository.existsById(1L)).willReturn(true);
        given(filterRepository.findByStudent(eq(Student.StudentBuilder.aStudent().withId(1L).build()))).willReturn(Optional.of(filter));

        var result = filterService.getGroupRecomMode(1L);
        assertFalse(result);
    }

    @Test
    public void getGroupRecomMode_getForNonExistentStudent_ThrowNotFoundException(){
        given(studentRepository.existsById(1L)).willReturn(false);

        var ex = assertThrows(NotFoundException.class, ()-> filterService.getGroupRecomMode(1L));
        assertEquals("Student with id 1 to get recommendation type for not found.", ex.getMessage());
    }

}
