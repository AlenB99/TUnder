package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.IndividualMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface IndividualMessageRepository extends JpaRepository<IndividualMessage, Long> {

    @Query("SELECT m FROM IndividualMessage m WHERE (m.relationship, m.timestamp) IN "
        + "(SELECT i.relationship, MAX(i.timestamp) FROM IndividualMessage i WHERE i.relationship IN :relationships AND i.timestamp < :timestamp GROUP BY i.relationship)")
    List<IndividualMessage> getLatestMessagePerRelationship(@Param("relationships") Collection<SingleRelationship> relationships, @Param("timestamp") Timestamp timestamp);

    List<IndividualMessage> getIndividualMessagesByRelationshipAndTimestampBeforeOrderByTimestampDesc(SingleRelationship relationship, Timestamp timestamp, PageRequest pageRequest);

    void deleteBySender(Student toDelete);

    void deleteIndividualMessageByRelationship(SingleRelationship singleRelationship);

    void deleteIndividualMessagesByRelationship(SingleRelationship singleRelationship);
}
