package socketchat.chat.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import socketchat.chat.domain.Group;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT COUNT(g) FROM Group g")
    int countGroup();
    @Query("SELECT g FROM Group g WHERE g.id = :id")
    Group findById(@Param("id") int id);
    @Query("SELECT g FROM Group g WHERE g.groupName LIKE %:groupName%")
    List<Group> findAllLikeGroupName(String groupName);
}
