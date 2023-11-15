package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LvaDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FilterMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class FilterMapperTest {

    @Autowired
    private FilterMapper filterMapper;

    @Test
    public void filterDtoToFilter_mapsLvas() {
        LvaDto lva1 = LvaDto.LvaDtoBuilder.aLvaDto().withId("112.12").build();
        LvaDto lva2 = LvaDto.LvaDtoBuilder.aLvaDto().withId("123.13").build();
        var lvaList = List.of(lva1, lva2);
        FilterDto filterDto = FilterDto.FilterDtoBuilder.aFilterDto().withStudent(new SimpleStudentDto(1L, null,"","","")).withLvas(lvaList).build();


        var filter = filterMapper.filterDtoToFilter(filterDto);
        assertEquals(2, filter.getLvas().size());
        assertEquals(1, filter.getLvas().stream().filter(lva -> lva.getId() == lva1.id()).count());
    }

    @Test
    public void filterDtoToFilter_setsAllValues() {
        FilterDto filterDto = FilterDto.FilterDtoBuilder.aFilterDto().withId(1L).withMinAge(20).withMaxAge(22).withGroupRecomMode(true).withStudent(new SimpleStudentDto(1L, null,"","","")).build();

        Filter filter = filterMapper.filterDtoToFilter(filterDto);

        assertAll(
            () -> assertEquals(filterDto.id(), filter.getId()),
            () -> assertEquals(filterDto.minAge(), filter.getMinAge()),
            () -> assertEquals(filterDto.maxAge(), filter.getMaxAge()),
            () -> assertEquals(filterDto.student().id(), filter.getStudent().getId()),
            () -> assertEquals(filterDto.groupRecomMode(), filter.getGroupRecomMode())
        );
    }

    @Test
    public void filterToFilterDto_setsAllValues() {
        Lva lva = Lva.LvaBuilder.aLvaBuilder().withId("112.12").withName("LVA1").build();
        List<Lva> lvas = new ArrayList<>();
        lvas.add(lva);
        Filter filter = Filter.FilterBuilder.aFilter().withId(1L).withMinAge(20).withMaxAge(22).withStudent(1L).withGroupRecomMode(true).withLvas(lvas).build();

        FilterDto filterDto = filterMapper.filterToFilterDto(filter);


        assertAll(
            () -> assertEquals(filter.getId(), filterDto.id()),
            () -> assertEquals(filter.getMinAge(), filterDto.minAge()),
            () -> assertEquals(filter.getMaxAge(), filterDto.maxAge()),
            () -> assertEquals(filter.getStudent().getId(), filterDto.id()),
            () -> assertEquals(1, filterDto.lvas().size()),
            () -> assertEquals(1, filterDto.lvas().stream().filter(lvaDto -> lvaDto.id() == lva.getId() && lvaDto.name() == lva.getName()).count()),
            () -> assertEquals(filter.getGroupRecomMode(), filterDto.groupRecomMode())
        );
    }
}
