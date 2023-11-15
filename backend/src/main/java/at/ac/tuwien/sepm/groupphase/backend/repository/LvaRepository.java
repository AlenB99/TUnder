package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to persist Students in.
 **/
@Repository
public interface LvaRepository extends JpaRepository<Lva, String> {

    /**
     * Method to retrieve an lva via id.
     **/
    Lva getLvaById(String id);

    /**
     * Method to retrieve on lva by their name.
     **/
    Lva findLvaByName(String name);

    /**
     * Method to retrieve all persisted lvas.
     **/
    List<Lva> findAll();

    /**
     * Method to retrieve list of persisted lvas the ids of which match the passed ids.
     **/
    List<Lva> findAllById(Iterable<String> id);

}
