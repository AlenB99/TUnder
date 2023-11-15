package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DemoGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DemoStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SingleRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/api/v1/match"})
public class MatchEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MatchService matchService;
    private final CustomMatchMapper matchMapper;
    private final StudentMapper studentMapper;
    private final RecommendationService recommendationService;
    private final RankService rankService;
    private final GroupMapper groupMapper;


    @Autowired
    public MatchEndpoint(MatchService matchService, StudentMapper studentMapper,
                         RecommendationService recommendationService, RankService rankService, GroupMapper groupMapper) {
        this.rankService = rankService;
        this.groupMapper = groupMapper;
        LOGGER.trace("Create new MatchEndpoint instance");
        this.matchService = matchService;
        this.recommendationService = recommendationService;
        this.matchMapper = new CustomMatchMapper();
        this.studentMapper = studentMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Update relationship of user to recommended user", security = @SecurityRequirement(name = "apiKey"))
    public SingleRelationshipDto postSingleRelationship(@Valid @RequestBody SingleRelationshipDto singleRelDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/match body: {}", singleRelDto);
        SingleRelationship singleRel = matchMapper.singleRelDtoToSingleRel(singleRelDto);
        return matchMapper.singleRelToSingleRelDto(
            matchService.postSingleRelationship(singleRel));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("demo/recalculate/{id}/{max}")
    @Operation(summary = "Update relationship of user to recommended user", security = @SecurityRequirement(name = "apiKey"))
    public List<DemoStudentDto> recalculateDemo(@PathVariable long id, @PathVariable int max) throws ValidationException {
        matchService.reCalculate(id, max);
        List<Pair<Student, Double>> pairList = recommendationService.getRecommendedStudentDemo(id);
        List<DemoStudentDto> demoStudentDtoList = new ArrayList<>();
        List<Student> studentList = new ArrayList<>();
        for (Pair<Student, Double> pair : pairList) {
            studentList.add(pair.getKey());
        }

        Map<Student, double[]> studentAndDistances = rankService.getDistancesForDemo(id, studentList);
        for (var element : studentAndDistances.keySet()) {
            demoStudentDtoList.add(
                new DemoStudentDto(
                    element.getFirstName(),
                    element.getLastName(),
                    studentAndDistances.get(element)[0],
                    studentAndDistances.get(element)[1],
                    studentAndDistances.get(element)[2],
                    studentAndDistances.get(element)[3],
                    studentAndDistances.get(element)[4],
                    studentAndDistances.get(element)[5],
                    studentAndDistances.get(element)[6]));
        }
        demoStudentDtoList.sort(Comparator.comparing(DemoStudentDto::overallDistance));
        int maxIndex = demoStudentDtoList.size();
        if (maxIndex > 10) {
            maxIndex = 10;
        }
        return demoStudentDtoList.subList(0, maxIndex);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get next recommended student", security = @SecurityRequirement(name = "apiKey"))
    public DetailedStudentDto getRecommended(@PathVariable Long id) throws ValidationException {
        LOGGER.info("GET /api/v1/match/{}", id);
        return studentMapper.studentToDetailedStudentDto(
            recommendationService.getRecommended(id)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("likes/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get likes of student", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleStudentDto> getLikes(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/match/{}", id);
        return matchService.getLikes(id);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "demo/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get ranked students", security = @SecurityRequirement(name = "apiKey"))
    public List<DemoStudentDto> getRankedStudents(@PathVariable Long studentId) throws ValidationException {
        LOGGER.info("GET /api/v1/filter/demo/student/{}", studentId);
        List<Pair<Student, Double>> pairList = recommendationService.getRecommendedStudentDemo(studentId);
        //Code line 135 distance 1 different from line 146
        List<Student> studentList = new ArrayList<>();

        List<DemoStudentDto> demoStudentDtoList = new ArrayList<>();

        for (Pair<Student, Double> pair : pairList) {
            studentList.add(pair.getKey());
        }

        Map<Student, double[]> studentAndDistances = rankService.getDistancesForDemo(studentId, studentList);
        //Code line 146 distance different from line 135
        for (var element : studentAndDistances.keySet()) {
            demoStudentDtoList.add(
                new DemoStudentDto(
                    element.getFirstName(),
                    element.getLastName(),
                    studentAndDistances.get(element)[0],
                    studentAndDistances.get(element)[1],
                    studentAndDistances.get(element)[2],
                    studentAndDistances.get(element)[3],
                    studentAndDistances.get(element)[4],
                    studentAndDistances.get(element)[5],
                    studentAndDistances.get(element)[6]));
        }
        demoStudentDtoList.sort(Comparator.comparing(DemoStudentDto::overallDistance));
        int maxIndex = demoStudentDtoList.size();
        if (maxIndex > 10) {
            maxIndex = 10;
        }
        return demoStudentDtoList.subList(0, maxIndex);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "demo/group/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get ranked groups", security = @SecurityRequirement(name = "apiKey"))
    public List<DemoGroupDto> getRankedGroups(@PathVariable Long studentId) {
        LOGGER.info("GET /api/v1/filter/demo/group/{}", studentId);
        List<Pair<Group, Double>> pairList = recommendationService.getRecommendedGroupDemo(studentId);
        List<DemoGroupDto> demoGroupDtoList = new ArrayList<>();
        for (Pair<Group, Double> pair : pairList) {
            demoGroupDtoList.add(new DemoGroupDto(pair.getKey().getName(), pair.getValue()));
        }
        return demoGroupDtoList;
    }
}
