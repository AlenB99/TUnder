package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomDescriptionGenerator {

    private List<String> descriptions;

    public RandomDescriptionGenerator() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = ResourceUtils.getFile("classpath:descriptions.json");
            Map<String, List<String>> map = mapper.readValue(file, new TypeReference<Map<String, List<String>>>() {});
            descriptions = map.get("descriptions");
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse descriptions file.", e);
        }
    }

    public String getRandomDescription() {
        if (descriptions == null || descriptions.isEmpty()) {
            throw new IllegalStateException("No descriptions available.");
        }
        Random random = new Random();
        return descriptions.get(random.nextInt(descriptions.size()));
    }
}
