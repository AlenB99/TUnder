package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SettingsMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.SettingsService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/settings")
public class SettingsEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    public SettingsEndpoint(SettingsService settingsService, SettingsMapper settingsMapper) {
        this.settingsService = settingsService;
        this.settingsMapper = settingsMapper;
    }

    @PermitAll
    @GetMapping("/{id}")
    public SettingsDto getByStudentId(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("get Settings");
        return settingsMapper.settingsToSettingsDto(settingsService.getSettingById(id));
    }

    @PermitAll
    @GetMapping("/byStudent")
    public SettingsDto getById(@RequestBody SettingsDto settingsDto) throws NotFoundException {
        LOGGER.info("get Settings");
        return settingsMapper.settingsToSettingsDto(settingsService.getSettingsByStudent(settingsDto));
    }

    @PermitAll
    @PutMapping("/{studentId}")
    public SettingsDto updateSettings(@PathVariable Long studentId, @RequestBody SettingsDto settingsDto) {
        LOGGER.info("update Settings");
        return settingsMapper.settingsToSettingsDto(settingsService.updateSettings(settingsDto, studentId));
    }
}
