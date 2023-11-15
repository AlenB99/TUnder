package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SingleRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MatchMappingTest {

    private final SingleRelationshipDto dto = new SingleRelationshipDto(
        6L,
        7L,
        RelStatus.LIKED
    );

    private final SingleRelationship entity = new SingleRelationship(
        new Student(8L),
        new Student(9L),
        RelStatus.LIKED
    );
    CustomMatchMapper mapper = new CustomMatchMapper();

    @Test
    public void convertSingleRelationshipDtoToSingleRelationship_shouldReturnSingleRelationshipEntityWithSameValues(){
        SingleRelationship mappedEntity = mapper.singleRelDtoToSingleRel(dto);
        assertThat(mappedEntity.getUserId()).isEqualTo(dto.userId());
        assertThat(mappedEntity.getRecommendedId()).isEqualTo(dto.recommendedId());
        assertThat(mappedEntity.getStatus()).isEqualTo(dto.status());
    }

    @Test
    public void convertSingleRelationshipToSingleRealtionshipDto_shouldReturnSingleRelationshipDtoWithSameValues(){
        SingleRelationshipDto mappedDto = mapper.singleRelToSingleRelDto(entity);
        assertThat(mappedDto.getUser().getId()).isEqualTo(entity.getUserId());
        assertThat(mappedDto.getRecommended().getId()).isEqualTo(entity.getRecommendedId());
        assertThat(mappedDto.status()).isEqualTo(entity.getStatus());
    }
}
