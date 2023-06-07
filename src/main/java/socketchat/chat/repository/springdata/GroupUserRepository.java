package socketchat.chat.repository.springdata;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE u.userId = :userId")
    List<GroupUser> findAllWithGroupAndUserByUserId(String userId);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE u.id = :id")
    List<GroupUser> findAllWithGroupAndUserByUserId(Long id);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE g.id = :groupId")
    List<GroupUser> findAllWithGroupAndUserByGroupId(int groupId);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user u JOIN FETCH gu.group g WHERE g.id = :groupId AND u.userId = :userId")
    GroupUser findAllWithGroupAndUserByGroupIdAndUserId(int groupId, String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupUser gu WHERE gu.groupUserId.userId = :userId AND gu.groupUserId.groupId = :groupId")
    void removeByUserIdAndGroupId(int groupId, String userId);

    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END FROM GroupUser gu WHERE gu.groupUserId.groupId = :groupId")
    Boolean existsUserByGroupId(int groupId);

    @Query("SELECT COUNT(gu) FROM GroupUser gu WHERE gu.groupUserId.groupId = :groupId")
    int countByGroupId(int groupId);

    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END FROM GroupUser gu WHERE gu.groupUserId.groupId = :groupId AND gu.groupUserId.userId = :userId")
    boolean existsUserAndGroup(String userId, int groupId);
}
