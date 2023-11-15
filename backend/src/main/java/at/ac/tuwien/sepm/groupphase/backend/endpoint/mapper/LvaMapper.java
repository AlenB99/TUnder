package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LvaDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

/**
 * Interface for mapping Lva entities to LvaDtos.
 **/

@Mapper
public interface LvaMapper {

    /**
     * Maps a lva entity object to a lva transfer object.
     *
     * @param lva entity object
     * @return transfer object of lva
     */
    LvaDto lvaToLvaDto(Lva lva);

    /**
     * Maps a List of Lva entities to a List of LvaDtos.
     *
     * @param lva list containing lva entity objects
     * @return list containing lva transfer objects
     */
    List<LvaDto> lvaToLvaDto(List<Lva> lva);

    /**
     * Maps a lva transfer object to a lva entity object.
     *
     * @param lva transfer object
     * @return entity object of lva
     */
    Lva lvaDtotoLva(LvaDto lva);



}
