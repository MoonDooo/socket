package socketchat.chat.repository.springdata;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import socketchat.chat.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserIdAndPassword(String userId, String password);

    @Query("SELECT u FROM User u JOIN FETCH GroupUser g Where u.id = :id")
    List<User> findByUserWithGroupUserById(String id);

    User findByUserId(String userId);
}
