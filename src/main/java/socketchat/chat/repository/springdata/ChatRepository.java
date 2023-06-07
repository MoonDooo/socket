package socketchat.chat.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import socketchat.chat.domain.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c JOIN FETCH c.groupUser gu WHERE gu.groupUserId.groupId = :groupId AND c.message LIKE %:message%")
    List<Chat> findAllWithGroupUserByGroupIdLikeMessage(int groupId, String message);

    @Query("SELECT c FROM Chat c JOIN FETCH c.groupUser gu WHERE gu.groupUserId.groupId = :groupId ORDER BY c.createdAt DESC")
    List<Chat> findAllWithGroupUserByGroupId(int groupId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.groupUser.groupUserId.groupId = :groupId AND c.groupUser.groupUserId.userId = :userId")
    void deleteAllByGroupUser(int groupId, String userId);
}
