package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Base64;
import java.util.List;

/**
 * Interface for mapping Student entities to DetailedStudentDtos and SimpleStudentDtos.
 **/

@Mapper
public interface StudentMapper {

    /**
     * Custom mapping method to map String to byte[].
     */
    default byte[] mapStringToByteArray(String value) {
        if (value != null) {
            return Base64.getMimeDecoder().decode(value.split(",")[1]);
        }
        return null;
    }

    /**
     * Custom mapping method to map byte[] to String.
     */
    default String mapByteArrayToString(byte[] value) {
        if (value != null) {
            return "data:image/jpeg;base64," + Base64.getMimeEncoder().encodeToString(value);
        }
        return null;
    }

    /**
     * Method for mapping 1 Student entity to 1 SimpleStudentDto.
     **/
    @Named("simpleStudent")
    SimpleStudentDto studentToSimpleStudentDto(Student student);

    /**
     * Method for mapping a list of Student entities to a List of SimpleStudentDtos.
     **/
    @IterableMapping(qualifiedByName = "simpleStudent")
    List<SimpleStudentDto> studentToSimpleStudentDtoList(List<Student> student);

    /**
     * Method for mapping a Student entity to a DetailedStudentDto.
     **/
    DetailedStudentDto studentToDetailedStudentDto(Student student);

    /**
     * Method for mapping a DetailedStudentDto  to a Student entity.
     **/
    Student detailedStudentDtoToStudent(DetailedStudentDto detailedStudentDto);

    /**
     * Method for mapping a Settings entity to a SettingsDto.
     **/
    SettingsDto settingsToSettingsDto(Settings settings);

    /**
     * Method for mapping a SettingsDto to a Settings entity.
     **/
    Settings settingsDtoToSettings(SettingsDto settingsDto);

    /**
     * Method for mapping a Filter entity to a FilterDto.
     **/
    FilterDto filterToFilterDto(Filter filter);

    /**
     * Method for mapping a FilterDto to a Filter entity.
     **/
    Filter filterDtoToFilter(FilterDto filter);

    /**
     * Method for updating a Student from a DetailedStudentDto.
     **/
    void updateStudentFromDto(DetailedStudentDto dto, @MappingTarget Student entity);

    List<DetailedStudentDto> studentsToDetailedStudentDtos(List<Student> students);
}

