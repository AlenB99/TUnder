package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.LvaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LvaServiceImplTest implements TestData {

    //TODO: Create LvaService tests
    @Autowired
    private LvaRepository lvaRepository;
    @Autowired
    private LvaServiceImpl lvaService ;

    @Test
    public void getLvaById_returnsCorrectLva() {
        Lva lva = lvaRepository.save(new Lva("1", "testLva",null,null));
        Lva result = lvaService.getLvaById("1");
        assertEquals(lva.getId(), result.getId());
        assertEquals(lva.getName(), result.getName());
    }

    @Test
    public void findAll_returnsAllStoredLvas() {
        Lva lva = lvaRepository.save(new Lva("1", "testLva",null,null));
        Lva lva2 = lvaRepository.save(new Lva("2", "testLva2",null,null));
        Lva lva3 = lvaRepository.save(new Lva("3", "testLva3",null,null));
        List<Lva> lvaList = lvaService.findAll();
        assertThat(lvaList.size()).isGreaterThanOrEqualTo(3);
        assertThat(lvaList)
            .map(Lva::getId, Lva::getName)
            .contains(tuple("1", "testLva"),
                tuple("2", "testLva2"),
                tuple("3", "testLva3")
            );
    }

    @Test
    public void findAllById_returnsAllMatchingLvas() {
        Lva lva = lvaRepository.save(new Lva("1", "testLva",null,null));
        Lva lva2 = lvaRepository.save(new Lva("2", "testLva2",null,null));
        Lva lva3 = lvaRepository.save(new Lva("3", "testLva3",null,null));
        List<String> idArr = new LinkedList<>();
        idArr.add("1");
        idArr.add("2");
        List<Lva> lvaList = lvaService.findAllById(idArr);
        assertThat(lvaList.size()).isGreaterThanOrEqualTo(2);
        assertThat(lvaList)
            .map(Lva::getId, Lva::getName)
            .contains(tuple("1", "testLva"),
                tuple("2", "testLva2")
            );
    }

    @Test
    public void persistLva_persistsLva() throws ValidationException {
        Lva lva = new Lva("123.678","lva",null,null);
        Lva result = lvaService.persistLva(lva);
        assertEquals(lva,result);
    }



}
