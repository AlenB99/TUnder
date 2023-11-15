package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class LanguageServiceImpl implements LanguageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LanguageRepository languageRepository;

    public LanguageServiceImpl(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public List<Language> findAll() {
        LOGGER.debug("Find all Languages");
        return languageRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public Language getLanguageById(String id) {
        LOGGER.debug("get Language by id: " + id);
        return languageRepository.getLanguageById(id);
    }

    @Override
    public List<Language> findAllById(Iterable<String> ids) {
        LOGGER.debug("Find Languages with matching ids");
        return languageRepository.findAllById(ids);
    }

    @Override
    public Language persistLanguage(Language language) throws ValidationException {
        LOGGER.trace("Persist new Language {}", language);
        return languageRepository.save(language);
    }

}
