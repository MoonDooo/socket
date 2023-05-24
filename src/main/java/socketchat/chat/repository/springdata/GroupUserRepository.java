package socketchat.chat.repository.springdata;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE u.userId = :userId")
    List<GroupUser> findAllWithGroupAndUserByUserId(String userId);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE u.id = :id")
    List<GroupUser> findAllWithGroupAndUserByUserId(Long id);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE g.id = :groupId")
    List<GroupUser> findAllWithGroupAndUserByGroupId(Long groupId);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE g.id = :groupId AND u.userId = :userId")
    GroupUser findAllWithGroupAndUserByGroupIdAndUserId(Long groupId, String userId);
}
