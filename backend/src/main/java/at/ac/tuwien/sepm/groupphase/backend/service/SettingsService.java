package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;

public interface SettingsService {
    Settings getSettingById(long id);

    Settings getSettingsByStudent(SettingsDto settingsDto);

    Settings updateSettings(SettingsDto settingsDto, long id);
}
