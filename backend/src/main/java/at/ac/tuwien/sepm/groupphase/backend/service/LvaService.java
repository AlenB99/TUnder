package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface LvaService {

    /**
     * Find all lva entries.
     *
     * @return list of all lva entries
     */
    List<Lva> findAll();

    /**
     * Get lva with corresponding id.
     *
     * @param id the id of the lva to be retrieved
     * @return lva with specified id
     */
    Lva getLvaById(String id) throws NotFoundException;

    /**
     * Find lva entries which ids match the passed ids.
     *
     * @return list of all lva entries with matching ids
     */
    List<Lva> findAllById(Iterable<String> ids);

    /**
     * Store Lva (@code Lva) in persistence data store.
     *
     * @param lva the lva to store
     * @return persisted lva entity
     */
    Lva persistLva(Lva lva) throws ValidationException;

}
