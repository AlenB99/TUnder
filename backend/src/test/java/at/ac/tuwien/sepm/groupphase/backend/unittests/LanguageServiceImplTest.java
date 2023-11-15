package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.LanguageServiceImpl;
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
public class LanguageServiceImplTest implements TestData {
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private LanguageServiceImpl languageService;

    @Test
    public void getLanguageById_returnsCorrectLanguage() {
        Language language = languageRepository.save(new Language("1", "testLanguage"));
        Language result = languageService.getLanguageById("1");
        assertEquals(language.getId(), result.getId());
        assertEquals(language.getName(), result.getName());
    }

    @Test
    public void findAll_returnsAllStoredLanguages() {
        Language language = languageRepository.save(new Language("1", "testLanguage"));
        Language language2 = languageRepository.save(new Language("2", "testLanguage2"));
        Language language3 = languageRepository.save(new Language("3", "testLanguage3"));
        List<Language> languageList = languageService.findAll();
        assertThat(languageList.size()).isGreaterThanOrEqualTo(3);
        assertThat(languageList)
            .map(Language::getId, Language::getName)
            .contains(tuple("1", "testLanguage"),
                tuple("2", "testLanguage2"),
                tuple("3", "testLanguage3")
            );
    }

    @Test
    public void findAllById_returnsAllMatchingLanguages() {
        Language language = languageRepository.save(new Language("1", "testLanguage"));
        Language language2 = languageRepository.save(new Language("2", "testLanguage2"));
        Language language3 = languageRepository.save(new Language("3", "testLanguage3"));
        List<String> idArr = new LinkedList<>();
        idArr.add("1");
        idArr.add("2");
        List<Language> languageList = languageService.findAllById(idArr);
        assertThat(languageList.size()).isGreaterThanOrEqualTo(2);
        assertThat(languageList)
            .map(Language::getId, Language::getName)
            .contains(tuple("1", "testLanguage"),
                tuple("2", "testLanguage2")
            );
    }



}
