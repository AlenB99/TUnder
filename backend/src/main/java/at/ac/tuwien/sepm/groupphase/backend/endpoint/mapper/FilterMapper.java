package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FilterDto;
import org.mapstruct.Mapper;

@Mapper(uses = {LvaMapper.class, StudentMapper.class})
public interface FilterMapper {

    /**
     * Maps a filter entity object to a filter transfer object.
     *
     * @param filter entity object
     * @return filter transfer object
     */
    FilterDto filterToFilterDto(Filter filter);

    /**
     * Maps a filter transfer object to a filter entity object.
     *
     * @param filter transfer object
     * @return filter entity object
     */
    Filter filterDtoToFilter(FilterDto filter);


}
