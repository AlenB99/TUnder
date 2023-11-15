package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class LvaRepositoryTest implements TestData {


    @Autowired
    private LvaRepository lvaRepository;

    @Test
    public void save_addsAndReturnsCorrectLva() {
        var lva1 = lvaRepository.save(new Lva("1", "lva1", null, null));
        var result = lvaRepository.getLvaById("1");
        assertEquals(lva1, result);
    }

    @Test
    public void getLvaById_returnsCorrectLva() {
        var lva1 = lvaRepository.save(new Lva("1", "lva1", null, null));
        var lva2 = lvaRepository.save(new Lva("2", "lva2", null, null));
        var lva3 = lvaRepository.save(new Lva("3", "lva3", null, null));
        var result = lvaRepository.getLvaById("1");
        assertEquals(lva1, result);
    }

    @Test
    public void getLvaById_withBadDataReturnsNull() {
        var lva1 = lvaRepository.save(new Lva("1", "lva1", null, null));
        var result = lvaRepository.getLvaById("2");
        assertNull(result);
    }

    @Test
    public void findAll_returnsAllLvas() {
        var lva1 = lvaRepository.save(new Lva("1", "lva1", null, null));
        var lva2 = lvaRepository.save(new Lva("2", "lva2", null, null));
        var lva3 = lvaRepository.save(new Lva("3", "lva3", null, null));
        List<Lva> lvaList = new LinkedList<>();
        lvaList.add(lva1);
        lvaList.add(lva2);
        lvaList.add(lva3);
        var result = lvaRepository.findAll();
        assertEquals(lvaList, result);
    }


}
