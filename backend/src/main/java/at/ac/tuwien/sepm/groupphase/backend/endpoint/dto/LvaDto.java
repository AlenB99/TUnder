package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record LvaDto(
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


    public static final class LvaDtoBuilder {
        String id;
        String name;

        public static LvaDtoBuilder aLvaDto() {
            return new LvaDtoBuilder();
        }

        public LvaDtoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public LvaDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LvaDto build() {
            return new LvaDto(id, name);
        }
    }
}
