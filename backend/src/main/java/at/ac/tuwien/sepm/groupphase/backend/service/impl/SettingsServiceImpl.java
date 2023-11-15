package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SettingsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final StudentRepository studentRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository, StudentRepository studentRepository) {
        this.settingsRepository = settingsRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Settings getSettingById(long id) {
        var f = settingsRepository.findByStudentId(id);
        return f.isPresent() ? f.get() : null;
    }

    @Override
    public Settings getSettingsByStudent(SettingsDto settingsDto) {
        var student = studentRepository.findStudentById(settingsDto.studentId());
        if (student == null) {
            throw new NotFoundException("could not find settings associated with student");
        }
        return settingsRepository.findByStudent(student);
    }

    @Override
    public Settings updateSettings(SettingsDto settingsDto, long id) {
        Optional<Settings> settingsOptional = settingsRepository.findByStudentId(id);
        if (!settingsOptional.isPresent()) {
            throw new NotFoundException("could not find settings");
        }
        Settings settings = settingsOptional.get();

        settings.setSubscribing(settingsDto.isSubscribing());
        settings.setHideAge(settingsDto.hideAge());
        settings.setHideGender(settingsDto.hideGender());
        settings.setHideEmail(settingsDto.hideEmail());
        settings.setHideFirstname(settingsDto.hideFirstName());
        settings.setHideLastname(settingsDto.hideLastName());
        settings.setSleeping(settingsDto.isSleeping());
        Settings newSettings = settingsRepository.save(settings);
        return newSettings;
    }
}
