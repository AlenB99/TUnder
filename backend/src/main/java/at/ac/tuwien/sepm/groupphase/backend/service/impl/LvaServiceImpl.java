package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.validator.LvaValidator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class LvaServiceImpl implements LvaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LvaRepository lvaRepository;
    private final LvaValidator validator;

    public LvaServiceImpl(LvaRepository lvaRepository, LvaValidator validator) {
        this.lvaRepository = lvaRepository;
        this.validator = validator;
    }

    @Override
    public List<Lva> findAll() {
        LOGGER.debug("Find all lvas");
        return lvaRepository.findAll();
    }

    @Override
    public Lva getLvaById(String id) {
        LOGGER.debug("get lva by id: " + id);
        return lvaRepository.getLvaById(id);
    }

    @Override
    public List<Lva> findAllById(Iterable<String> ids) {
        LOGGER.debug("Find lvas with matching ids");
        return lvaRepository.findAllById(ids);
    }

    @Override
    public Lva persistLva(Lva lva) throws ValidationException {
        LOGGER.trace("Persist new Lva {}", lva);
        validator.validateLva(lva);
        return lvaRepository.save(lva);
    }

}
