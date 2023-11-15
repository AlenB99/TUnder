package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record LanguageDto(
    String id,
    String name
) {
    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }


    public static final class LanguageDtoBuilder {
        String id;
        String name;

        public static LanguageDtoBuilder aLanguageDto() {
            return new LanguageDtoBuilder();
        }

        public LanguageDtoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public LanguageDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LanguageDto build() {
            return new LanguageDto(id, name);
        }
    }
}
