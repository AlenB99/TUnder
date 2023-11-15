package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SettingsService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SettingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettingsServiceImplTest {

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private SettingsServiceImpl settingsService;

    private Settings settings;
    private SettingsDto settingsDto;
    private Student student;

    @BeforeEach
    public void setup() {
        settings = new Settings();
        settings.setSubscribing(true);
        student = new Student();
        student.setId(1L);
        settingsDto = new SettingsDto(1L,false,true,false,false,false,false, false,student.getId(),null);
    }

    @Test
    public void getSettingById_existingId_shouldReturnSettings() {
        when(settingsRepository.findByStudentId(student.getId())).thenReturn(Optional.of(settings));
        Settings result = settingsService.getSettingById(student.getId());
        assertNotNull(result);
        assertEquals(settings, result);
    }

    @Test
    public void getSettingById_nonExistingId_shouldReturnNull() {
        when(settingsRepository.findByStudentId(student.getId())).thenReturn(Optional.empty());
        Settings result = settingsService.getSettingById(student.getId());
        assertNull(result);
    }

    @Test
    public void getSettingsByStudent_existingStudent_shouldReturnSettings() {
        when(studentRepository.findStudentById(student.getId())).thenReturn(student);
        when(settingsRepository.findByStudent(student)).thenReturn(settings);
        Settings result = settingsService.getSettingsByStudent(settingsDto);
        assertNotNull(result);
        assertEquals(settings, result);
    }

    @Test
    public void getSettingsByStudent_nonExistingStudent_shouldThrowNotFoundException() {
        when(studentRepository.findStudentById(student.getId())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> settingsService.getSettingsByStudent(settingsDto));
    }

    @Test
    public void updateSettings_existingSettings_shouldReturnUpdatedSettings() {
        Settings updatedSettings = new Settings();
        updatedSettings.setSubscribing(false);
        when(settingsRepository.findByStudentId(student.getId())).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(updatedSettings);
        Settings result = settingsService.updateSettings(settingsDto, student.getId());
        assertNotNull(result);
        assertEquals(updatedSettings, result);
    }

    @Test
    public void updateSettings_nonExistingSettings_shouldThrowNotFoundException() {
        when(settingsRepository.findByStudentId(student.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> settingsService.updateSettings(settingsDto, student.getId()));
    }
}