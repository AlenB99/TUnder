package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Component
public class TestNameGenerator {
    private static final String FIRST_NAMES_FILE = "classpath:FirstNames.json";
    private static final String LAST_NAMES_FILE = "classpath:LastNames.json";
    private List<String> firstNames;
    private List<String> lastNames;

    public TestNameGenerator(ApplicationContext applicationContext) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream firstNameStream = applicationContext.getResource(FIRST_NAMES_FILE).getInputStream();
            InputStream lastNameStream = applicationContext.getResource(LAST_NAMES_FILE).getInputStream();

            HashMap<String, List<String>> firstNameMap = mapper.readValue(firstNameStream, HashMap.class);
            HashMap<String, List<String>> lastNameMap = mapper.readValue(lastNameStream, HashMap.class);

            firstNames = firstNameMap.get("firstNames");
            lastNames = lastNameMap.get("lastNames");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomLastName() {
        Random rand = new Random();
        String lastName = lastNames.get(rand.nextInt(lastNames.size()));
        return lastName;
    }

    public String getRandomFirstName() {
        Random rand = new Random();
        String firstName = firstNames.get(rand.nextInt(firstNames.size()));
        return firstName;
    }
}