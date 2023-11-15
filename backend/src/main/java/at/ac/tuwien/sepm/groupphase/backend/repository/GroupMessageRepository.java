package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMessage;
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
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    @Query("SELECT m FROM GroupMessage m WHERE (m.receiver, m.timestamp) IN "
        + "(SELECT i.receiver, MAX(i.timestamp) FROM GroupMessage i WHERE i.receiver IN :receiver AND i.timestamp < :timestamp GROUP BY i.receiver)")
    List<GroupMessage> getGroupMessagesByReceiverInAndTimestampBeforeOrderByTimestampDesc(@Param("receiver") Collection<Group> receiver, @Param("timestamp") Timestamp timestamp);

    List<GroupMessage> getGroupMessagesByReceiverAndTimestampBeforeOrderByTimestampDesc(Group receiver, Timestamp timestamp, PageRequest pageable);

    List<GroupMessage> findBySender(Student s);

    void deleteBySender(Student toDelete);
}
