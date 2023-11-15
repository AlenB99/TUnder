package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to persist Languages in.
 **/
@Repository
public interface LanguageRepository extends JpaRepository<Language, String> {

    /**
     * Method to retrieve a language via id.
     **/
    Language getLanguageById(String id);

    /**
     * Method to retrieve one language by their name.
     **/
    Language findLanguageByName(String name);

    /**
     * Method to retrieve all persisted languages.
     **/
    List<Language> findAll();

    /**
     * Method to retrieve list of persisted languages the ids of which match the passed ids.
     **/
    List<Language> findAllById(Iterable<String> id);

}
