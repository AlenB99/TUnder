package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LanguageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Interface for mapping Language entities to LanguageDtos.
 **/

@Mapper
public interface LanguageMapper {

    /**
     * Maps a language entity object to a language transfer object.
     *
     * @param language entity object
     * @return transfer object of language
     */
    LanguageDto languageToLanguageDto(Language language);

    /**
     * Maps a List of Language entities to a List of LanguageDtos.
     *
     * @param language list containing language entity objects
     * @return list containing language transfer objects
     */
    List<LanguageDto> languageToLanguageDto(List<Language> language);

    /**
     * Maps a language transfer object to a language entity object.
     *
     * @param language transfer object
     * @return entity object of language
     */
    Language languageDtotoLanguage(LanguageDto language);



}
