package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Component
public class TestCourseGenerator {
    private static final String COURSES_FILE = "courses.json";
    private List<String> courses;

    public TestCourseGenerator() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File courseFile = new ClassPathResource(COURSES_FILE).getFile();

            HashMap<String, List<String>> courseMap = mapper.readValue(courseFile, HashMap.class);

            courses = courseMap.get("classes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomCourse(int id) {
        Random rand = new Random();
        return courses.get(id);
    }
}
