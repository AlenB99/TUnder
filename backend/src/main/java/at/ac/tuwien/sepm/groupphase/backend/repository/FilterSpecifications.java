package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class FilterSpecifications {
    public static Specification<Student> filterSpecificationsStudent(Filter filter, Long studentId) {
        Specification<Student> ageSpec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getMinAge() != null) {
                LocalDate minDate = LocalDate.now().minusYears(filter.getMinAge());
                Date minDateLiteral = Date.valueOf(minDate);

                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateOfBirth"), minDateLiteral));
            }

            if (filter.getMaxAge() != null) {
                LocalDate maxDate = LocalDate.now().minusYears(filter.getMaxAge() + 1);
                maxDate = maxDate.plusDays(1);

                Date maxDateLiteral = Date.valueOf(maxDate);

                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateOfBirth"), maxDateLiteral));
            }

            return predicate;
        };
        Specification<Student> ignoreAlreadyRecommended = (root, query, cb) -> {

            Subquery<Long> recommended = query.subquery(Long.class);
            Root<SingleRelationship> recommendedRoot = recommended.from(SingleRelationship.class);
            recommended.select(recommendedRoot.get("user"));
            recommended.where(cb.and(cb.equal(recommendedRoot.get("recommended"), studentId), cb.notEqual(recommendedRoot.get("status"), RelStatus.LIKED)));

            Subquery<Long> recommendee = query.subquery(Long.class);
            Root<SingleRelationship> recommendeeRoot = recommendee.from(SingleRelationship.class);
            recommendee.select(recommendeeRoot.get("recommended"));
            recommendee.where(cb.equal(recommendeeRoot.get("user"), studentId));

            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.not(cb.in(root.get("id")).value(recommendee)));
            predicate = cb.and(predicate, cb.not(cb.in(root.get("id")).value(recommended)));
            predicate = cb.and(predicate, cb.not(cb.equal(root.get("id"), studentId)));

            return predicate;
        };

        Specification<Student> lvaSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getLvas() != null && filter.getLvas().size() > 0) {
                List<String> lvaIds = filter.getLvas().stream().map(Lva::getId).toList();
                Join<Student, Lva> lvaJoin = root.join("currentLvas");
                predicate = cb.and(predicate, lvaJoin.get("id").in(lvaIds));
            }
            return predicate;
        });

        Specification<Student> meetsIrlSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getMeetsIrl() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("meetsIrl"), filter.getMeetsIrl()));
            }
            return predicate;
        });

        Specification<Student> prefLanguageSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getPrefLanguage() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("prefLanguage"), filter.getPrefLanguage()));
            }
            return predicate;
        });

        Specification<Student> ignoreSleepMode = (root, query, cb) -> {
            Join<Student, Settings> settingsJoin = root.join("settings",  JoinType.LEFT);
            return cb.or(
                cb.isNull(settingsJoin.get("isSleeping")),
                cb.isFalse(settingsJoin.get("isSleeping"))
            );
        };
        Specification<Student> ignoreNotEnabled = ((root, query, cb) -> cb.isTrue(root.get("enabled")));

        return ageSpec.and(ignoreAlreadyRecommended).and(lvaSpec).and(meetsIrlSpec)
            .and(prefLanguageSpec).and(ignoreSleepMode).and(ignoreNotEnabled);
    }

    public static Specification<Group> filterSpecificatonsGroup(Filter filter, Long studentId) {
        Specification<Group> lvaSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getLvas() != null && filter.getLvas().size() > 0) {
                List<String> lvaIds = filter.getLvas().stream().map(Lva::getId).toList();
                Join<Group, Lva> lvaJoin = root.join("lvas");
                predicate = cb.and(predicate, lvaJoin.get("id").in(lvaIds));
            }
            return predicate;
        });

        Specification<Group> meetsIrlSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getMeetsIrl() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("meetsIrl"), filter.getMeetsIrl()));
            }
            return predicate;
        });

        Specification<Group> prefLanguageSpec = ((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getPrefLanguage() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("prefLanguage"), filter.getPrefLanguage()));
            }
            return predicate;
        });

        Specification<Group> ignoreAlreadyIn = (root, query, cb) -> {

            // Create a subquery to check if any group member has the specified studentId
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<GroupMember> subqueryRoot = subquery.from(GroupMember.class);
            subquery.select(subqueryRoot.get("studyGroup").get("id"));
            subquery.where(cb.equal(subqueryRoot.get("student").get("id"), studentId));

            // Add a NOT EXISTS condition to the main query, to retrieve groups that don't have any member with the studentId
            return cb.not(cb.in(root.get("id")).value(subquery));
        };

        Specification<Group> ignoreAlreadyRecommended = (root, query, cb) -> {

            Subquery<Long> groupIds = query.subquery(Long.class);
            Root<GroupRelationship> groupRelRoot = groupIds.from(GroupRelationship.class);
            groupIds.select(groupRelRoot.get("recommendedGroup"));
            groupIds.where(cb.equal(groupRelRoot.get("user"), studentId));

            return cb.not(cb.in(root.get("id")).value(groupIds));
        };

        return lvaSpec.and(lvaSpec).and(meetsIrlSpec).and(prefLanguageSpec).and(ignoreAlreadyIn).and(ignoreAlreadyRecommended);

    }
}
