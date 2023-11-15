package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Language;

import java.util.List;

public record FilterDto(
    Long id,
    SimpleStudentDto student,
    Integer minAge,
    Integer maxAge,
    List<LvaDto> lvas,
    Language prefLanguage,
    Boolean meetsIrl,
    Boolean groupRecomMode
) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public SimpleStudentDto student() {
        return student;
    }

    @Override
    public Integer minAge() {
        return minAge;
    }

    @Override
    public Integer maxAge() {
        return maxAge;
    }

    @Override
    public List<LvaDto> lvas() {
        return lvas;
    }

    public Language prefLanguage() {
        return prefLanguage;
    }

    public static class FilterDtoBuilder {
        private Long id;
        private SimpleStudentDto studentId;
        private Integer minAge;
        private Integer maxAge;
        private List<LvaDto> lvas;
        private Language prefLanguage;
        private Boolean meetsIrl;
        private Boolean groupRecomMode;

        public static FilterDtoBuilder aFilterDto() {
            return new FilterDtoBuilder();
        }

        public FilterDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public FilterDtoBuilder withStudent(SimpleStudentDto student) {
            this.studentId = student;
            return this;
        }

        public FilterDtoBuilder withMinAge(Integer minAge) {
            this.minAge = minAge;
            return this;
        }

        public FilterDtoBuilder withMaxAge(Integer maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public FilterDtoBuilder withLvas(List<LvaDto> lvas) {
            this.lvas = lvas;
            return this;
        }

        public FilterDtoBuilder withPrefLanguage(Language prefLanguage) {
            this.prefLanguage = prefLanguage;
            return this;
        }

        public FilterDtoBuilder withMeetsIrl(Boolean meetsIrl) {
            this.meetsIrl = meetsIrl;
            return this;
        }

        public FilterDtoBuilder withGroupRecomMode(Boolean groupRecomMode) {
            this.groupRecomMode = groupRecomMode;
            return this;
        }


        public FilterDto build() {
            return new FilterDto(this.id, this.studentId, this.minAge, this.maxAge, this.lvas, this.prefLanguage, this.meetsIrl, this.groupRecomMode);
        }
    }
}

