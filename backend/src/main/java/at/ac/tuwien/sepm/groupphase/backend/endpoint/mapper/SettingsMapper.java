package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SettingsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SettingsMapper {
    @Mapping(target = "isSleeping", source = "sleeping")
    @Mapping(target = "isSubscribing", source = "subscribing")
    @Mapping(target = "hideLastName", source = "hideLastname")
    @Mapping(target = "hideFirstName", source = "hideFirstname")
    SettingsDto settingsToSettingsDto(Settings settings);
}
