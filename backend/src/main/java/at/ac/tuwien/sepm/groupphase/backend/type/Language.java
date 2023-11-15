package at.ac.tuwien.sepm.groupphase.backend.type;

import java.util.Random;

public enum Language {
    German,
    English,
    Turkish,
    Serbian,
    Croatian,
    Hungarian,
    Romanian,
    Polish,
    Russian,
    Italian,
    French;

    public static Language getRandomLanguage() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
