package socketchat.chat.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import socketchat.chat.domain.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c JOIN FETCH GroupUser gu WHERE gu.group.id = :groupId AND c.message LIKE %:message%")
    List<Chat> findAllWithGroupUserByGroupIdLikeMessage(Long groupId, String message);

    @Query("SELECT c FROM Chat c JOIN FETCH GroupUser gu WHERE gu.group.id = :groupId ORDER BY createdAt DESC")
    List<Chat> findAllWithGroupUserByGroupId(Long groupId);
}
