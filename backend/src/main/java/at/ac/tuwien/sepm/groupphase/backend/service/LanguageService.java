package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface LanguageService {

    /**
     * Find all language entries.
     *
     * @return list of all language entries
     */
    List<Language> findAll();

    /**
     * Get Language with corresponding id.
     *
     * @param id the id of the Language to be retrieved
     * @return Language with specified id
     */
    Language getLanguageById(String id) throws NotFoundException;

    /**
     * Find Language entries which ids match the passed ids.
     *
     * @return list of all Language entries with matching ids
     */
    List<Language> findAllById(Iterable<String> ids);

    /**
     * Store Language (@code Language) in persistence data store.
     *
     * @param language the Language to store
     * @return persisted Language entity
     */
    Language persistLanguage(Language language) throws ValidationException;

}
